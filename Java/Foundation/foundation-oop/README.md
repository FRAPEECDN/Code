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

Each section also has a standalone entry point in `com.fp.coding`: `ModelExamples`, `EnumExamples`, `InheritanceExamples`, `GenericExamples`, `ObjectsOptionalExamples`, `DepartmentExamples`, `UuidPojoExamples`, `CollectionExamples`, and `CollectionEvolutionExamples`.

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

Each module has the same five test classes. `FoundationalExamplesTest` covers the core examples and menu flows; `DepartmentTest` covers department assignments; `NameValidationTest` covers accepted names and Unicode normalization; `UuidPojoInformationTest` covers copying, ordering, and serialization; and `CollectionExamplesTest` covers searching, collection/stream examples, and that module's collection-evolution examples. The Java-version-specific examples are smoke-tested through their output.
