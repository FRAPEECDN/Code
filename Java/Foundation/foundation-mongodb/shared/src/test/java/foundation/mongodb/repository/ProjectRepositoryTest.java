package foundation.mongodb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import foundation.mongodb.model.Project;
import foundation.mongodb.model.ProjectOwner;
import foundation.mongodb.model.ProjectStatus;

/**
 * Integration tests for the repository using the in-memory MongoDB
 * wire-protocol server.
 */
class ProjectRepositoryTest {
    private MongoServer server;
    private MongoClient client;
    private ProjectRepository projects;

    @BeforeEach
    void setUp() {
        server = new MongoServer(new MemoryBackend());
        client = MongoClients.create(server.bindAndGetConnectionString());
        MongoDatabase database = client.getDatabase("foundation-test");
        projects = new ProjectRepository(database);
    }

    @AfterEach
    void tearDown() {
        client.close();
        server.shutdown();
    }

    /** Exercises inserts, lookups, search, replacement, and deletion. */
    @Test
    void supportsCrudSearchAndLookups() {
        ProjectOwner owner = new ProjectOwner();
        Project project = project(owner, "Atlas migration", "Northwind", 125_000);

        projects.create(project);

        assertEquals(project, projects.findById(project.getProjectId()).orElseThrow());
        assertEquals(1, projects.findByOwnerId(owner.getId()).size());
        assertEquals(project, projects.findByStatus(ProjectStatus.REGISTERED).get(0));
        assertEquals(project, projects.search("ATLAS").get(0));
        assertEquals(project, projects.search("northwind").get(0));
        assertEquals(1, projects.findAll().size());

        project.transitionTo(ProjectStatus.PLANNED, LocalDate.of(2026, 2, 1));
        projects.save(project);
        Project stored = projects.findById(project.getProjectId()).orElseThrow();
        assertEquals(ProjectStatus.PLANNED, stored.getStatus());
        assertEquals(project.getStatusHistory(), stored.getStatusHistory());
        assertEquals(1, projects.findByStatus(ProjectStatus.PLANNED).size());

        assertTrue(projects.delete(project.getProjectId()));
        assertFalse(projects.delete(project.getProjectId()));
        assertTrue(projects.findAll().isEmpty());
    }

    /** Confirms save uses upsert semantics for a new project. */
    @Test
    void saveInsertsWhenProjectDoesNotExist() {
        Project project = project(new ProjectOwner(), "New project", "Contoso", 0);

        projects.save(project);

        assertTrue(projects.findById(project.getProjectId()).isPresent());
    }

    /**
     * Confirms owner lookup reconstructs a shared owner for all returned projects.
     */
    @Test
    void ownerLookupReconstructsOneOwnerForAllProjects() {
        ProjectOwner owner = new ProjectOwner();
        projects.create(project(owner, "Alpha", "Contoso", 10));
        projects.create(project(owner, "Beta", "Contoso", 20));

        var storedProjects = projects.findByOwnerId(owner.getId());

        assertEquals(2, storedProjects.size());
        assertEquals(2, storedProjects.get(0).getProjectOwner().getProjects().size());
        assertEquals(storedProjects.get(0).getProjectOwner(), storedProjects.get(1).getProjectOwner());
    }

    /**
     * Exercises numeric/date filters, paging, counts, aggregation, and distinct
     * values.
     */
    @Test
    void supportsFiltersPagingCountsAndDistinctValues() {
        assertThrows(IllegalArgumentException.class, () -> projects.bulkSave(List.of()));

        ProjectOwner owner = new ProjectOwner();
        Project alpha = project(owner, "Alpha", "Contoso", 100);
        Project beta = project(owner, "Beta", "Northwind", 200);
        Project gamma = project(owner, "Gamma", "Contoso", 300);
        projects.bulkSave(List.of(alpha, beta, gamma));

        assertEquals(3, projects.count());
        assertEquals(3, projects.countByStatus(ProjectStatus.REGISTERED));
        assertTrue(projects.exists(alpha.getProjectId()));
        assertFalse(projects.exists(UUID.randomUUID()));
        assertEquals(List.of(beta, gamma), projects.findByBudgetRange(200, 300));
        assertEquals(List.of(beta), projects.findPage(1, 1));
        assertEquals(List.of("Contoso", "Northwind"), projects.findDistinctSponsors());
        assertEquals(600.0, projects.totalBudgetByStatus().get(ProjectStatus.REGISTERED));
        assertEquals(3, projects.findByDeadlineRange(
                LocalDate.of(2026, 12, 31), LocalDate.of(2026, 12, 31)).size());
    }

    /** Verifies optimistic status updates and owner-scoped deletion. */
    @Test
    void supportsAtomicStatusUpdatesAndScopedDeletes() {
        ProjectOwner owner = new ProjectOwner();
        Project registered = project(owner, "Registered", "Contoso", 10);
        Project cancelled = project(owner, "Cancelled", "Contoso", 20);
        cancelled.transitionTo(ProjectStatus.CANCEL, LocalDate.of(2026, 2, 1));
        projects.create(registered);
        projects.create(cancelled);

        assertTrue(projects.updateStatus(registered.getProjectId(), ProjectStatus.PLANNED,
                LocalDate.of(2026, 2, 2)));
        assertFalse(projects.updateStatus(UUID.randomUUID(), ProjectStatus.PLANNED,
                LocalDate.of(2026, 2, 2)));
        assertEquals(ProjectStatus.PLANNED,
                projects.findById(registered.getProjectId()).orElseThrow().getStatus());
        assertEquals(1, projects.deleteByOwnerIdAndStatus(owner.getId(), ProjectStatus.CANCEL));
        assertEquals(1, projects.deleteByOwnerId(owner.getId()));
        assertEquals(0, projects.count());
    }

    /**
     * Runs only when a real replica-set URI is configured; the in-memory server
     * lacks these features.
     */
    @Test
    void supportsTextSearchAndTransactionsOnConfiguredReplicaSet() {
        String uri = System.getenv("MONGODB_INTEGRATION_URI");
        Assumptions.assumeTrue(uri != null && !uri.isBlank(),
                "Set MONGODB_INTEGRATION_URI to run text-search and transaction integration checks");

        try (MongoClient integrationClient = MongoClients.create(uri)) {
            String databaseName = "foundation-test-" + UUID.randomUUID();
            MongoDatabase database = integrationClient.getDatabase(databaseName);
            ProjectRepository integrationProjects = new ProjectRepository(integrationClient, database);
            integrationProjects.ensureTextSearchIndex();
            Project project = project(new ProjectOwner(), "Atlas observability", "Northwind", 50);

            integrationProjects.saveAllTransactionally(List.of(project));

            assertEquals(1, integrationProjects.textSearch("Atlas").size());
            database.drop();
        }
    }

    private Project project(ProjectOwner owner, String name, String sponsor, double budget) {
        return Project.builder()
                .projectOwner(owner)
                .projectName(name)
                .sponsor(sponsor)
                .budget(budget)
                .startDate(LocalDate.of(2026, 1, 1))
                .deadline(LocalDate.of(2026, 12, 31))
                .registeredOn(LocalDate.of(2026, 1, 1))
                .build();
    }
}
