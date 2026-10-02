# Foundation Collection Examples

This Gradle multi-project build demonstrates Java collection choices using the existing `Project`, `ProjectOwner`, and `ProjectStatus` domain classes. The same sample projects are used by Java 17, 21, and 25 so the version-specific APIs can be compared without changing the data.

## Run

Run a version independently:

```powershell
.\gradlew.bat :java17:run
.\gradlew.bat :java21:run
.\gradlew.bat :java25:run
```

Build and test every module:

```powershell
.\gradlew.bat :java17:build :java21:build :java25:build
```

Gradle toolchains select the Java version for each module. `shared/src/main/java/com/fp/coding/ProjectCollectionDemo.java` creates the sample projects and coordinates five focused examples: `JdkCollectionsDemo`, `GoogleCollectionsDemo`, `ApacheCollectionsDemo`, `EclipseCollectionsDemo`, and `JavaTuplesDemo`. The Java 21 sequenced-collection walkthrough is isolated in `shared-java21/src/main/java/com/fp/coding/SequencedCollectionsDemo.java` and compiled only into Java 21 and Java 25. `Java25GatherersDemo` is local to Java 25.

## Java Collections Framework

| Implementation | What it demonstrates | Advantages | Disadvantages | Concurrent alternative |
| --- | --- | --- | --- | --- |
| `ArrayList` | Mutable indexed list CRUD | Fast indexed reads; amortized constant-time append | Middle insertions and removals shift elements | `CopyOnWriteArrayList` for read-heavy workloads; writes copy the array |
| `LinkedList` | Deque ends and encounter order | Efficient operations at either end | Slow indexed access; node and pointer overhead | `ConcurrentLinkedDeque` for concurrent deque operations; not a `List` replacement |
| `HashSet` | Uniqueness and membership | Average constant-time add, contains, and remove | No encounter order; equality and hash values must remain stable | `ConcurrentHashMap.newKeySet()` |
| `LinkedHashSet` | Unique values in encounter order | Predictable iteration order with hash-based lookup | Extra memory for order links | `ConcurrentHashMap.newKeySet()` if order is not required |
| `TreeSet` | Sorted unique projects | Automatically sorted; logarithmic operations | Comparator cost; comparator-equal values collapse | `ConcurrentSkipListSet` |
| `EnumSet` | Lifecycle-state set | Compact and fast for one enum type | Keys must be from one enum | `ConcurrentHashMap.newKeySet()` |
| `HashMap` | Key/value CRUD | Average constant-time lookup and update | No ordering guarantee | `ConcurrentHashMap` |
| `LinkedHashMap` | Insertion-ordered key/value CRUD | Predictable encounter order | Extra memory for order links | `ConcurrentHashMap` if order is unnecessary |
| `TreeMap` | Sorted project-name map | Sorted keys; logarithmic operations | More overhead than a hash map | `ConcurrentSkipListMap` |
| `EnumMap` | Counts keyed by lifecycle state | Compact and fast for enum keys | Keys must be from one enum | `ConcurrentHashMap` |

The project model's mutable `status` participates in Lombok-generated `equals` and `hashCode`. Do not change status while a project is stored in a hash-based set or used as a hash-map key. The demos keep hash-set values stable while stored.

## Collection Algorithms and Streams

The baseline sorts projects by budget and name. Linear search works on unsorted input in O(n); binary search is O(log n) but requires the input to be sorted using the same comparator. The streams use predicates to filter projects, map them to names or budgets, calculate total and average budgets, and group projects by lifecycle status.

Stream intermediate operations are lazy. Keep stream functions stateless and non-interfering. Parallel streams can help with sufficiently large workloads, but only when the source, operations, and reductions are safe for concurrent execution.

## Third-Party Collections

| Library and implementation | What it demonstrates | Advantages | Disadvantages | Concurrent alternative |
| --- | --- | --- | --- | --- |
| Guava `ImmutableList` | Immutable project snapshot | Cannot be structurally modified; safe to share after safe publication | Shallow snapshot only: elements remain mutable; copying references costs allocation | Keep immutable snapshots and publish them safely |
| Guava `ArrayListMultimap` | Multiple projects per status | Convenient list-valued mapping; preserves value order per key | Mutable and not thread-safe; key order is unspecified | `Multimaps.synchronizedListMultimap`; synchronize on the wrapper while iterating |
| Commons `CollectionUtils.select` | Predicate-based selection | Concise filtering for an `Iterable` | Traverses input and allocates a result collection | Use a concurrent source or synchronize while traversing |
| Commons `ArrayListValuedHashMap` | Mutable multi-valued status map | Multiple values per key with list behavior | Mutable and not thread-safe; key order is unspecified | `ConcurrentHashMap<K, CopyOnWriteArrayList<V>>` for read-heavy access |
| Eclipse `MutableList` | Mutable list CRUD | Rich collection API and efficient iteration | Mutable and not thread-safe | `CopyOnWriteArrayList` for read-heavy access |
| Eclipse `MutableSet` | Mutable set CRUD | Rich API with unique membership | Mutable and not thread-safe | `ConcurrentHashMap.newKeySet()` |
| Eclipse `MutableMap` | Mutable key/value CRUD | Rich map API | Mutable and not thread-safe | `ConcurrentHashMap` |

The named concurrent alternatives are guidance, not part of the sequential examples. Some change ordering guarantees or write costs; choose based on the required semantics rather than substituting by name alone.

## JavaTuples

The examples cover `Tuple0` through `Tuple10`, the named `Empty`, `Solo`, `Pair`, `Trio`, `Quartet`, `Quintet`, `Sextet`, `Septet`, `Octet`, and `Decet` types, plus `KeyValue`, `LabelValue`, and `MapEntry`. The demo prints arity and values, reads positions, and uses `Pair.withValue1` to show immutable derivation: the original tuple remains unchanged.

Tuple values are immutable and can be shared after safe publication. Prefer a domain class or record when a tuple's positions need durable names and meaning across an API.

## Version Progression

- [Java 17](java17/README.md): common collections, CRUD, algorithms, streams, third-party libraries, and tuples.
- [Java 21](java21/README.md): adds `SequencedCollection`, `SequencedSet`, and `SequencedMap` (JEP 431) for first/last and reversed-order operations.
- [Java 25](java25/README.md): adds no new collection interfaces beyond Java 21; it reuses the same sequenced demo and runs an additional Stream Gatherers example. Gatherers are a Stream API feature finalized in JDK 24 (JEP 485), demonstrated here by the Java 25 application.
