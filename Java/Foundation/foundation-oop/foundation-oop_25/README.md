# Foundation OOP - Java 25

Exercises and examples for object-oriented programming using Java 25.

Includes enums, a validated POJO and record, sealed inheritance, generic bounds and wildcards, module imports, unnamed record-pattern components, flexible constructor bodies, a department assignment model, and a UUID-identified serializable POJO. Start the section menu with `com.fp.coding.ExampleMenu`; standalone sections are `ModelExamples`, `EnumExamples`, `InheritanceExamples`, `GenericExamples`, `ObjectsOptionalExamples`, `DepartmentExamples`, `UuidPojoExamples`, `CollectionExamples`, and `CollectionEvolutionExamples`.

`CollectionExamples` demonstrates common `List`, `Set`, and `Map` operations, sorting and linear/binary search, and sequential/parallel Stream pipelines using `UuidPojoInformation`. `CollectionEvolutionExamples` demonstrates sequenced collections and Java 25 Stream Gatherers.

Build this module from the project root:

```sh
mvn -pl foundation-oop-25 -am test
```

After building, run the menu from the project root with `java -cp foundation-oop-25/target/classes com.fp.coding.ExampleMenu`.
