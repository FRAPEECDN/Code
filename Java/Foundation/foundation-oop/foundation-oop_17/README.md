# Foundation OOP - Java 17

Exercises and examples for object-oriented programming using Java 17.

Includes enums, a validated POJO and record, sealed inheritance, generic bounds and wildcards, a department assignment model, and a UUID-identified serializable POJO. Start the section menu with `com.fp.coding.ExampleMenu`; standalone sections are `ModelExamples`, `EnumExamples`, `InheritanceExamples`, `GenericExamples`, `ObjectsOptionalExamples`, `DepartmentExamples`, `UuidPojoExamples`, `CollectionExamples`, and `CollectionEvolutionExamples`.

`CollectionExamples` demonstrates common `List`, `Set`, and `Map` operations, sorting and linear/binary search, and sequential/parallel Stream pipelines using `UuidPojoInformation`.

`CollectionEvolutionExamples` demonstrates the immutable collection factories introduced in Java 9, `Stream.toList()` from Java 16, and the `mapMulti()` Stream operation.

Build this module from the project root:

```sh
mvn -pl foundation_oop_17 -am test
```

After building, run the menu from the project root with `java -cp foundation_oop_17/target/classes com.fp.coding.ExampleMenu`.
