# Foundation OOP - Java 21

Exercises and examples for object-oriented programming using Java 21.

Includes enums, a validated POJO and record, sealed inheritance, generic bounds and wildcards, checked and unchecked exception examples, try-with-resources, record-pattern switches, sequenced collections, a department assignment model, and a UUID-identified serializable POJO. Start the section menu with `com.fp.coding.ExampleMenu`; standalone sections include `GenericExamples` and `ExceptionHandlingExamples` as well as `ModelExamples`, `EnumExamples`, `InheritanceExamples`, `ObjectsOptionalExamples`, `DepartmentExamples`, `UuidPojoExamples`, `CollectionExamples`, and `CollectionEvolutionExamples`.

`GenericShowcase<T>` demonstrates a generic class, wildcard producers (`? extends T`), wildcard consumers (`? super T`), unbounded wildcards, and bounded generic methods. `ExceptionHandlingExamples` contrasts a checked `MissingNameException` with an unchecked `InvalidCapacityException`, propagates `IOException`, and uses try-with-resources to close a file reader on both success and failure. Choose section 10 for these exception examples; section 11 runs every section.

`CollectionExamples` demonstrates common `List`, `Set`, and `Map` operations, sorting and linear/binary search, and sequential/parallel Stream pipelines using `UuidPojoInformation`. `CollectionEvolutionExamples` demonstrates Java 21 sequenced collections.

Build this module from the project root:

```sh
mvn -pl foundation_oop_21 -am test
```

After building, run the menu from the project root with `java -cp foundation_oop_21/target/classes com.fp.coding.ExampleMenu`.
