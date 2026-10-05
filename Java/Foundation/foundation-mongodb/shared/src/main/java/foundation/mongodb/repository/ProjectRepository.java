package foundation.mongodb.repository;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;

import foundation.mongodb.model.Project;
import foundation.mongodb.model.ProjectOwner;
import foundation.mongodb.model.ProjectStatus;

/**
 * MongoDB persistence operations for {@link Project} documents in the
 * {@code projects} collection.
 *
 * <p>
 * The repository maps model objects to BSON documents explicitly. Project and
 * owner UUIDs are
 * stored as strings, dates as ISO-8601 strings, and lifecycle history as an
 * embedded document.
 * The supplied {@link MongoClient} is never closed by this class. Use the
 * database-only
 * constructor for operations that do not need transactions.
 */
public final class ProjectRepository {
    private final MongoClient client;
    private final MongoCollection<Document> collection;

    /**
     * Creates a repository without transaction support and ensures lookup indexes
     * exist.
     *
     * @param database database containing the {@code projects} collection
     * @throws NullPointerException if {@code database} is null
     */
    public ProjectRepository(MongoDatabase database) {
        this(null, database);
    }

    /**
     * Creates a repository with transaction support and ensures lookup indexes
     * exist.
     *
     * @param client   client used to start transaction sessions; may be null when
     *                 transactions are unused
     * @param database database containing the {@code projects} collection
     * @throws NullPointerException if {@code database} is null
     */
    public ProjectRepository(MongoClient client, MongoDatabase database) {
        this.client = client;
        this.collection = database.getCollection("projects");
        this.collection.createIndex(Indexes.ascending("ownerId"));
        this.collection.createIndex(Indexes.ascending("status"));
    }

    /**
     * Inserts a project document. The project UUID is the MongoDB {@code _id}.
     *
     * @param project project to insert
     * @throws com.mongodb.MongoWriteException if a document with the same project
     *                                         UUID already exists
     * @throws NullPointerException            if {@code project} is null
     */
    public void create(Project project) {
        collection.insertOne(toDocument(project));
    }

    /**
     * Replaces the document identified by the project's UUID, inserting it when
     * absent.
     *
     * @param project complete project state to persist
     * @throws NullPointerException if {@code project} is null
     */
    public void save(Project project) {
        collection.replaceOne(eq("_id", project.getProjectId().toString()), toDocument(project),
                new ReplaceOptions().upsert(true));
    }

    /**
     * Replaces or inserts a project using the supplied transaction session.
     *
     * @param session active session associated with this repository's MongoClient
     * @param project complete project state to persist
     * @throws NullPointerException if {@code session} or {@code project} is null
     */
    public void save(ClientSession session, Project project) {
        collection.replaceOne(session, eq("_id", project.getProjectId().toString()), toDocument(project),
                new ReplaceOptions().upsert(true));
    }

    /**
     * Saves all supplied projects atomically in a multi-document transaction.
     *
     * <p>
     * The repository must have been constructed with a {@link MongoClient}.
     * Transactions
     * require a replica set or sharded cluster; standalone MongoDB servers do not
     * support them.
     *
     * @param projects projects to upsert as one atomic bulk operation
     * @throws IllegalStateException    if this repository has no client
     * @throws IllegalArgumentException if {@code projects} is empty
     * @throws NullPointerException     if {@code projects} or one of its elements
     *                                  is null
     */
    public void saveAllTransactionally(List<Project> projects) {
        if (client == null) {
            throw new IllegalStateException("transaction support requires a MongoClient");
        }
        try (ClientSession session = client.startSession()) {
            session.withTransaction(() -> {
                bulkSave(session, projects);
                return null;
            });
        }
    }

    /**
     * Upserts multiple projects in one non-transactional bulk write.
     *
     * @param projects nonempty projects to upsert
     * @return MongoDB's acknowledged bulk-write counts and upsert identifiers
     * @throws IllegalArgumentException if {@code projects} is empty
     * @throws NullPointerException     if {@code projects} or one of its elements
     *                                  is null
     */
    public BulkWriteResult bulkSave(List<Project> projects) {
        return collection.bulkWrite(replaceModels(projects));
    }

    /**
     * Upserts multiple projects in one bulk write using an existing session.
     *
     * @param session  active session associated with this repository's MongoClient
     * @param projects nonempty projects to upsert
     * @return MongoDB's acknowledged bulk-write counts and upsert identifiers
     * @throws IllegalArgumentException if {@code projects} is empty
     * @throws NullPointerException     if {@code session}, {@code projects}, or one
     *                                  of its elements is null
     */
    public BulkWriteResult bulkSave(ClientSession session, List<Project> projects) {
        return collection.bulkWrite(session, replaceModels(projects));
    }

    /**
     * Applies one lifecycle transition with an atomic compare-and-set update.
     *
     * <p>
     * The stored status is included in the update filter so a concurrent transition
     * cannot
     * silently overwrite a newer state. Lifecycle and date-order validation use the
     * model's
     * {@link Project#transitionTo(ProjectStatus, LocalDate)} rules.
     *
     * @param projectId  project UUID
     * @param nextStatus target state
     * @param reachedOn  date the target state was reached
     * @return {@code true} if the document was updated; {@code false} if it was
     *         missing or changed concurrently
     * @throws IllegalStateException    if the transition is not allowed
     * @throws IllegalArgumentException if the transition date precedes the previous
     *                                  state date
     * @throws NullPointerException     if a required argument is null for an
     *                                  existing project
     */
    public boolean updateStatus(UUID projectId, ProjectStatus nextStatus, LocalDate reachedOn) {
        Optional<Project> existing = findById(projectId);
        if (existing.isEmpty()) {
            return false;
        }

        Project project = existing.get();
        ProjectStatus previousStatus = project.getStatus();
        project.transitionTo(nextStatus, reachedOn);
        UpdateResult result = collection.updateOne(
                com.mongodb.client.model.Filters.and(
                        eq("_id", projectId.toString()),
                        eq("status", previousStatus.name())),
                Updates.combine(
                        Updates.set("status", nextStatus.name()),
                        Updates.set("statusHistory." + nextStatus.name(), reachedOn.toString())));
        return result.getModifiedCount() == 1;
    }

    /**
     * Finds one project by its persistent UUID.
     *
     * @param projectId project UUID
     * @return the project, or empty when no matching document exists
     */
    public Optional<Project> findById(UUID projectId) {
        return Optional.ofNullable(collection.find(eq("_id", projectId.toString())).first())
                .map(document -> fromDocument(document, new HashMap<>()));
    }

    /**
     * Finds an owner's projects, ordered by project name ascending.
     *
     * @param ownerId persistent owner UUID
     * @return matching projects; projects in this result share one reconstructed
     *         owner instance
     */
    public List<Project> findByOwnerId(UUID ownerId) {
        return decode(collection.find(eq("ownerId", ownerId.toString())).sort(Sorts.ascending("projectName")));
    }

    /**
     * Finds projects in the specified lifecycle state, ordered by project name
     * ascending.
     *
     * @param status lifecycle state to match
     * @return matching projects
     */
    public List<Project> findByStatus(ProjectStatus status) {
        return decode(collection.find(eq("status", status.name())).sort(Sorts.ascending("projectName")));
    }

    /**
     * Searches project names and sponsors for a case-insensitive literal substring.
     *
     * <p>
     * The term is quoted before being used as a regular expression, so regex
     * metacharacters
     * in user input are treated literally. This search does not use a MongoDB text
     * index.
     *
     * @param term literal text to find
     * @return matching projects, ordered by project name ascending
     * @throws NullPointerException if {@code term} is null
     */
    public List<Project> search(String term) {
        String expression = Pattern.quote(term);
        return decode(collection.find(or(
                Filters.regex("projectName", expression, "i"),
                Filters.regex("sponsor", expression, "i"))).sort(Sorts.ascending("projectName")));
    }

    /**
     * Returns every project ordered by project name ascending.
     *
     * @return all stored projects
     */
    public List<Project> findAll() {
        return decode(collection.find().sort(Sorts.ascending("projectName")));
    }

    /**
     * Returns one zero-based page of projects ordered by project name ascending.
     *
     * @param page     zero-based page number
     * @param pageSize maximum number of projects to return
     * @return projects in the requested page, possibly empty
     * @throws IllegalArgumentException if {@code page} is negative or
     *                                  {@code pageSize} is not positive
     * @throws ArithmeticException      if the calculated MongoDB skip offset
     *                                  exceeds the supported integer range
     */
    public List<Project> findPage(int page, int pageSize) {
        if (page < 0 || pageSize < 1) {
            throw new IllegalArgumentException("page must be nonnegative and pageSize must be positive");
        }
        int offset = Math.toIntExact((long) page * pageSize);
        return decode(collection.find().sort(Sorts.ascending("projectName")).skip(offset).limit(pageSize));
    }

    /**
     * Finds projects whose budgets fall within the inclusive range.
     *
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @return matching projects ordered by project name ascending
     * @throws IllegalArgumentException if a bound is non-finite or minimum exceeds
     *                                  maximum
     */
    public List<Project> findByBudgetRange(double minimum, double maximum) {
        if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || minimum > maximum) {
            throw new IllegalArgumentException("budget range must be finite and minimum must not exceed maximum");
        }
        return decode(collection.find(com.mongodb.client.model.Filters.and(
                gte("budget", minimum), lte("budget", maximum))).sort(Sorts.ascending("projectName")));
    }

    /**
     * Finds projects whose deadlines fall within the inclusive date range.
     *
     * @param start inclusive first deadline
     * @param end   inclusive last deadline
     * @return matching projects ordered by deadline ascending
     * @throws IllegalArgumentException if {@code start} is after {@code end}
     * @throws NullPointerException     if either date is null
     */
    public List<Project> findByDeadlineRange(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must not be after end");
        }
        return decode(collection.find(com.mongodb.client.model.Filters.and(
                gte("deadline", start.toString()), lte("deadline", end.toString())))
                .sort(Sorts.ascending("deadline")));
    }

    /**
     * Counts all project documents.
     *
     * @return number of stored projects
     */
    public long count() {
        return collection.countDocuments();
    }

    /**
     * Counts project documents in the specified lifecycle state.
     *
     * @param status lifecycle state to count
     * @return number of matching projects
     */
    public long countByStatus(ProjectStatus status) {
        return collection.countDocuments(eq("status", status.name()));
    }

    /**
     * Checks whether a project document exists for the supplied UUID.
     *
     * @param projectId project UUID
     * @return {@code true} if a matching document exists
     */
    public boolean exists(UUID projectId) {
        return collection.countDocuments(eq("_id", projectId.toString())) > 0;
    }

    /**
     * Deletes every project belonging to an owner.
     *
     * @param ownerId persistent owner UUID
     * @return number of deleted documents
     */
    public long deleteByOwnerId(UUID ownerId) {
        return collection.deleteMany(eq("ownerId", ownerId.toString())).getDeletedCount();
    }

    /**
     * Deletes projects matching both an owner and lifecycle state.
     *
     * @param ownerId persistent owner UUID
     * @param status  lifecycle state to match
     * @return number of deleted documents
     */
    public long deleteByOwnerIdAndStatus(UUID ownerId, ProjectStatus status) {
        return collection.deleteMany(com.mongodb.client.model.Filters.and(
                eq("ownerId", ownerId.toString()), eq("status", status.name()))).getDeletedCount();
    }

    /**
     * Returns the distinct sponsor names, sorted case-insensitively.
     *
     * @return distinct sponsor names
     */
    public List<String> findDistinctSponsors() {
        List<String> sponsors = collection.distinct("sponsor", String.class).into(new ArrayList<>());
        sponsors.sort(String::compareToIgnoreCase);
        return sponsors;
    }

    /**
     * Aggregates the sum of project budgets for each lifecycle state.
     *
     * @return status-to-budget-total map; statuses with no projects are absent
     */
    public Map<ProjectStatus, Double> totalBudgetByStatus() {
        Map<ProjectStatus, Double> totals = new HashMap<>();
        MongoIterable<Document> results = collection.aggregate(List.of(
                Aggregates.group("$status", Accumulators.sum("totalBudget", "$budget"))));
        for (Document result : results) {
            totals.put(ProjectStatus.valueOf(result.getString("_id")),
                    result.get("totalBudget", Number.class).doubleValue());
        }
        return totals;
    }

    /**
     * Ensures the compound MongoDB text index used by {@link #textSearch(String)}
     * exists.
     *
     * <p>
     * Text indexes are supported by MongoDB but not by the project's in-memory test
     * server.
     */
    public void ensureTextSearchIndex() {
        collection.createIndex(Indexes.compoundIndex(
                Indexes.text("projectName"), Indexes.text("sponsor")));
    }

    /**
     * Searches the MongoDB text index over project name and sponsor.
     *
     * @param term MongoDB text-search expression
     * @return matching projects ordered by project name ascending
     * @throws com.mongodb.MongoCommandException if the text index has not been
     *                                           created
     */
    public List<Project> textSearch(String term) {
        return decode(collection.find(Filters.text(term)).sort(Sorts.ascending("projectName")));
    }

    /**
     * Deletes one project by UUID.
     *
     * @param projectId project UUID
     * @return {@code true} if one document was deleted
     */
    public boolean delete(UUID projectId) {
        return collection.deleteOne(eq("_id", projectId.toString())).getDeletedCount() > 0;
    }

    private List<WriteModel<Document>> replaceModels(List<Project> projects) {
        Objects.requireNonNull(projects, "projects");
        if (projects.isEmpty()) {
            throw new IllegalArgumentException("projects must not be empty");
        }
        List<WriteModel<Document>> writes = new ArrayList<>();
        for (Project project : projects) {
            writes.add(new ReplaceOneModel<>(eq("_id", project.getProjectId().toString()),
                    toDocument(project), new ReplaceOptions().upsert(true)));
        }
        return writes;
    }

    private List<Project> decode(Iterable<Document> documents) {
        List<Project> projects = new ArrayList<>();
        Map<UUID, ProjectOwner> owners = new HashMap<>();
        for (Document document : documents) {
            projects.add(fromDocument(document, owners));
        }
        return projects;
    }

    private Document toDocument(Project project) {
        Document history = new Document();
        for (Map.Entry<ProjectStatus, LocalDate> entry : project.getStatusHistory().entrySet()) {
            history.append(entry.getKey().name(), entry.getValue().toString());
        }
        return new Document("_id", project.getProjectId().toString())
                .append("ownerId", project.getProjectOwner().getId().toString())
                .append("projectName", project.getProjectName())
                .append("sponsor", project.getSponsor())
                .append("budget", project.getBudget())
                .append("startDate", project.getStartDate().toString())
                .append("deadline", project.getDeadline().toString())
                .append("status", project.getStatus().name())
                .append("statusHistory", history);
    }

    private Project fromDocument(Document document, Map<UUID, ProjectOwner> owners) {
        Document storedHistory = document.get("statusHistory", Document.class);
        UUID ownerId = UUID.fromString(document.getString("ownerId"));
        ProjectOwner owner = owners.computeIfAbsent(ownerId, ProjectOwner::new);
        Project project = Project.builder()
                .projectId(UUID.fromString(document.getString("_id")))
                .projectOwner(owner)
                .projectName(document.getString("projectName"))
                .sponsor(document.getString("sponsor"))
                .budget(document.get("budget", Number.class).doubleValue())
                .startDate(LocalDate.parse(document.getString("startDate")))
                .deadline(LocalDate.parse(document.getString("deadline")))
                .registeredOn(LocalDate.parse(storedHistory.getString(ProjectStatus.REGISTERED.name())))
                .build();

        for (ProjectStatus status : ProjectStatus.values()) {
            if (status != ProjectStatus.REGISTERED && storedHistory.containsKey(status.name())) {
                project.transitionTo(status, LocalDate.parse(storedHistory.getString(status.name())));
            }
        }
        return project;
    }
}
