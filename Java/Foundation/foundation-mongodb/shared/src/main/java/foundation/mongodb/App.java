package foundation.mongodb;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import foundation.mongodb.model.Project;
import foundation.mongodb.model.ProjectOwner;
import foundation.mongodb.model.ProjectStatus;
import foundation.mongodb.repository.ProjectRepository;

/**
 * Runs a MongoDB repository demonstration against the configured deployment.
 *
 * <p>
 * The demo uses stable, namespaced IDs so repeated runs upsert the same sample
 * records.
 * It removes only records belonging to its dedicated demo owner before exiting.
 * Configure
 * {@code MONGODB_URI} for authenticated deployments and
 * {@code MONGODB_DATABASE} to select
 * another database.
 */
public final class App {
    private static final UUID DEMO_OWNER_ID = UUID.nameUUIDFromBytes(
            "foundation-mongodb-demo-owner".getBytes(StandardCharsets.UTF_8));

    private App() {
    }

    /**
     * Connects to MongoDB, demonstrates the repository operations, and removes demo
     * records.
     *
     * <p>
     * The default connection string is {@code mongodb://localhost:27017}; the
     * default
     * database is {@code foundation}. Multi-document transactions are demonstrated
     * only
     * when the server reports replica-set membership.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        String connectionString = System.getenv().getOrDefault(
                "MONGODB_URI", "mongodb://localhost:27017");
        String databaseName = System.getenv().getOrDefault("MONGODB_DATABASE", "foundation");

        try (MongoClient client = MongoClients.create(connectionString)) {
            MongoDatabase database = client.getDatabase(databaseName);
            ProjectRepository projects = new ProjectRepository(client, database);
            projects.ensureTextSearchIndex();

            ProjectOwner owner = new ProjectOwner(DEMO_OWNER_ID);
            Project atlas = project("foundation-mongodb-demo-atlas", owner,
                    "Atlas observability", "Northwind", 125_000);
            atlas.transitionTo(ProjectStatus.PLANNED, LocalDate.of(2026, 2, 1));
            Project data = project("foundation-mongodb-demo-data", owner,
                    "Data platform", "Contoso", 90_000);
            data.transitionTo(ProjectStatus.CANCEL, LocalDate.of(2026, 2, 2));

            System.out.printf("Bulk upsert result: %s%n", projects.bulkSave(List.of(atlas, data)));
            if (supportsTransactions(client)) {
                projects.saveAllTransactionally(List.of(atlas, data));
                System.out.println("Transactional bulk save completed.");
            } else {
                System.out.println("Connected MongoDB is standalone; transaction demo skipped.");
            }
            projects.updateStatus(atlas.getProjectId(), ProjectStatus.IMPLEMENTATING,
                    LocalDate.of(2026, 3, 1));

            System.out.printf("By ID: %s%n", projects.findById(atlas.getProjectId()).orElseThrow());
            System.out.printf("By owner: %d; by status: %d%n",
                    projects.findByOwnerId(DEMO_OWNER_ID).size(),
                    projects.findByStatus(ProjectStatus.IMPLEMENTATING).size());
            System.out.printf("All projects: %d%n", projects.findAll().size());
            System.out.printf("Regex search: %d; text search: %d%n",
                    projects.search("Atlas").size(), projects.textSearch("Atlas").size());
            System.out.printf("Page: %d; budget range: %d; deadline range: %d%n",
                    projects.findPage(0, 10).size(), projects.findByBudgetRange(50_000, 150_000).size(),
                    projects.findByDeadlineRange(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1)).size());
            System.out.printf("Count: %d; status count: %d; exists: %s%n",
                    projects.count(), projects.countByStatus(ProjectStatus.IMPLEMENTATING),
                    projects.exists(atlas.getProjectId()));
            System.out.printf("Budgets by status: %s; sponsors: %s%n",
                    projects.totalBudgetByStatus(), projects.findDistinctSponsors());

            Project disposable = project("foundation-mongodb-demo-disposable", owner,
                    "Disposable example", "Foundation", 1);
            projects.create(disposable);
            System.out.printf("Deleted by ID: %s%n", projects.delete(disposable.getProjectId()));
            System.out.printf("Deleted cancelled demo projects: %d%n",
                    projects.deleteByOwnerIdAndStatus(DEMO_OWNER_ID, ProjectStatus.CANCEL));
            System.out.printf("Deleted remaining demo projects: %d%n",
                    projects.deleteByOwnerId(DEMO_OWNER_ID));
        }
    }

    /**
     * Checks the server handshake for replica-set membership, which is required for
     * transactions.
     *
     * @param client connected MongoDB client
     * @return {@code true} when the server advertises a replica-set name
     */
    private static boolean supportsTransactions(MongoClient client) {
        Document hello = client.getDatabase("admin").runCommand(new Document("hello", 1));
        return hello.containsKey("setName");
    }

    /**
     * Builds one deterministic demo project with the supplied owner and details.
     *
     * @param id      stable seed used to derive the project UUID
     * @param owner   project owner
     * @param name    project name
     * @param sponsor project sponsor
     * @param budget  project budget
     * @return a registered project with the common demo dates
     */
    private static Project project(String id, ProjectOwner owner, String name, String sponsor, double budget) {
        return Project.builder()
                .projectId(UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8)))
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
