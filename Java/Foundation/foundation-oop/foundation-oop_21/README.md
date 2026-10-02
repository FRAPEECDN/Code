# Foundation OOP - Java 21

Exercises and examples for object-oriented programming using Java 21.

Includes enums, a validated POJO and record, sealed inheritance, generic bounds and wildcards, record-pattern switches, sequenced collections, a department assignment model, and a UUID-identified serializable POJO. Start the section menu with `com.fp.coding.ExampleMenu`; standalone sections are `ModelExamples`, `EnumExamples`, `InheritanceExamples`, `GenericExamples`, `ObjectsOptionalExamples`, `DepartmentExamples`, `UuidPojoExamples`, `CollectionExamples`, and `CollectionEvolutionExamples`.

`CollectionExamples` demonstrates common `List`, `Set`, and `Map` operations, sorting and linear/binary search, and sequential/parallel Stream pipelines using `UuidPojoInformation`. `CollectionEvolutionExamples` demonstrates Java 21 sequenced collections.

Build this module from the project root:

```sh
mvn -pl foundation_oop_21 -am test
```

After building, run the menu from the project root with `java -cp foundation_oop_21/target/classes com.fp.coding.ExampleMenu`.
