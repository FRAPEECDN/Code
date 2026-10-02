package foundation.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import foundation.jdbc.model.Project;
import foundation.jdbc.model.ProjectOwner;
import foundation.jdbc.model.ProjectStatus;
import foundation.jdbc.repository.H2ProjectRepository;
import foundation.jdbc.repository.SQLiteProjectRepository;

class ProjectRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void persistsCrudAndLifecycleHistory() {
        String jdbcUrl = "jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
        ProjectOwner owner = new ProjectOwner();
        Project project = newProject(owner, "Launch portal");

        try (H2ProjectRepository repository = new H2ProjectRepository(jdbcUrl, "sa", "")) {
            long id = repository.create(project);

            Project restored = repository.findById(id).orElseThrow();
            assertEquals("Launch portal", restored.getProjectName());
            assertEquals(ProjectStatus.REGISTERED, restored.getStatus());
            assertEquals(LocalDate.of(2026, 1, 2),
                    restored.getStatusHistory().get(ProjectStatus.REGISTERED));
            assertEquals(restored.getProjectOwner().getId(), owner.getId());

            Project anotherProject = newProject(owner, "Reporting dashboard");
            long anotherId = repository.create(anotherProject);
            assertEquals(2, repository.findAll().size());
            assertEquals(2, repository.findAll().get(0).getProjectOwner().getProjects().size());

            restored.transitionTo(ProjectStatus.PLANNED, LocalDate.of(2026, 1, 5));
            assertTrue(repository.update(id, restored));
            Project updated = repository.findById(id).orElseThrow();
            assertEquals(ProjectStatus.PLANNED, updated.getStatus());
            assertEquals(LocalDate.of(2026, 1, 5), updated.getStatusHistory().get(ProjectStatus.PLANNED));

            assertTrue(repository.deleteById(anotherId));
            assertFalse(repository.deleteById(anotherId));
            assertTrue(repository.findById(anotherId).isEmpty());
        }
    }

    @Test
    void persistsCrudAndLifecycleHistoryInSqlite() {
        ProjectOwner owner = new ProjectOwner();
        Project project = newProject(owner, "SQLite project");
        String jdbcUrl = "jdbc:sqlite:" + tempDir.resolve("projects.db");

        try (SQLiteProjectRepository repository = new SQLiteProjectRepository(jdbcUrl)) {
            long id = repository.create(project);
            Project restored = repository.findById(id).orElseThrow();
            assertEquals("SQLite project", restored.getProjectName());
            assertEquals(owner.getId(), restored.getProjectOwner().getId());
            assertEquals(ProjectStatus.REGISTERED, restored.getStatus());

            restored.transitionTo(ProjectStatus.PLANNED, LocalDate.of(2026, 1, 5));
            assertTrue(repository.update(id, restored));
            assertEquals(ProjectStatus.PLANNED, repository.findById(id).orElseThrow().getStatus());
            assertTrue(repository.deleteById(id));
            assertTrue(repository.findById(id).isEmpty());
        }
    }

    private static Project newProject(ProjectOwner owner, String name) {
        return Project.builder()
                .projectOwner(owner)
                .projectName(name)
                .sponsor("Foundation")
                .budget(1500.0)
                .startDate(LocalDate.of(2026, 2, 1))
                .deadline(LocalDate.of(2026, 12, 1))
                .registeredOn(LocalDate.of(2026, 1, 2))
                .build();
    }
}