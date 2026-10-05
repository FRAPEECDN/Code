# Foundation OOP

Maven multi-module project for object-oriented programming foundations across Java releases.

## Modules

- `foundation-oop-17` targets Java 17.
- `foundation-oop-21` targets Java 21.
- `foundation-oop-25` targets Java 25.

Each module inherits dependencies, compiler settings, and test tooling from the parent POM.

## Examples

The modules recreate the enum, POJO, and record examples in `com.fp.coding`. Each also includes a sealed `Staff` parent with a `final` `Manager` child and a `non-sealed` `Developer` child, plus a generic showcase for the POJO, record, and both staff types.

Start the section-based console menu with `com.fp.coding.ExampleMenu`. It groups the POJO/record, enum, inheritance, generics, `Objects`/`Optional`, department, UUID/interface, and collection examples, and includes a run-all option. The collection section uses `UuidPojoInformation` in `List`, `Set`, and UUID-keyed `Map` examples with standard mutations/queries, sorting, linear/binary search, and stream pipelines. `CollectionEvolutionExamples` covers immutable collection factories, `Stream.toList()`, and `mapMulti()` in Java 17; Java 21 adds record-pattern switches and sequenced collections; Java 25 adds module imports, unnamed record-pattern components, flexible constructor bodies, and Stream Gatherers. The Java 17 generic section also demonstrates pattern matching for `instanceof`.

Each section also has a standalone entry point in `com.fp.coding`: `ModelExamples`, `EnumExamples`, `InheritanceExamples`, `GenericExamples`, `ObjectsOptionalExamples`, `DepartmentExamples`, `UuidPojoExamples`, `CollectionExamples`, `CollectionEvolutionExamples`, and `ExceptionHandlingExamples`.

### Generics

`GenericShowcase<T>` is a generic class that stores values of one chosen type. Its `addAll(Collection<? extends T>)` method reads values from a producer whose element type is `T` or a subtype. Its `copyTo(Collection<? super T>)` method writes values to a consumer whose element type is `T` or a supertype. This is the PECS rule: **producer extends, consumer super**. `displayWildcard(Collection<?>)` demonstrates a collection whose element type is unknown, while bounded generic methods demonstrate `<T extends Information>` and `<T extends Comparable<? super T>>`.

The tests instantiate `GenericShowcase<Information>`, add both a POJO and a record, and copy the values into `ArrayList<Object>`. That example shows why Java generic collections are invariant: `List<PojoInformation>` is not a `List<Information>`, but a method accepting `Collection<? extends Information>` can safely read values from it.

### Exception Handling

`ExceptionHandlingExamples` compares two custom validation failures:

- `MissingNameException extends Exception` is checked. `requireName` declares it, so calling code must catch it or declare it in its own `throws` clause.
- `InvalidCapacityException extends IllegalArgumentException` is unchecked. `requirePositiveCapacity` may throw it, but Java does not require callers to catch or declare it.
- `readFirstLine(Path)` declares `IOException`, a checked exception from file access. Its try-with-resources block closes the `BufferedReader` whether reading succeeds or throws. The test deletes the file immediately after the method returns to verify the reader has been closed.

All three Java-version modules contain and test these same concepts. The exception example is menu section 10; choose 11 to run every section.

After building with the JDK matching the selected module, start its menu from this directory:

```sh
java -cp foundation_oop_17/target/classes com.fp.coding.ExampleMenu
```

## Build

Run all modules from this directory with Maven and a JDK that meets each module's configured Java version:

```sh
mvn test
```

## Tests

Each module has six test classes. `FoundationalExamplesTest` covers core examples and menu flows; `DepartmentTest` covers department assignments; `ExceptionHandlingExamplesTest` covers checked/unchecked exception contracts and try-with-resources cleanup; `NameValidationTest` covers accepted names and Unicode normalization; `UuidPojoInformationTest` covers copying, ordering, and serialization; and `CollectionExamplesTest` covers searching, collection/stream examples, and that module's collection-evolution examples. The Java-version-specific examples are smoke-tested through their output.
