# Java 17 Collections

This module is the baseline walkthrough. It uses `Project` values to demonstrate Java `List`, `Set`, and `Map` implementations; CRUD; sorting; linear and binary search; predicate/filter/map stream pipelines; budget totals and averages; status grouping; Guava, Apache Commons Collections, Eclipse Collections, and JavaTuples.

`ProjectCollectionDemo` creates the sample projects and delegates to focused classes: `JdkCollectionsDemo`, `GoogleCollectionsDemo`, `ApacheCollectionsDemo`, `EclipseCollectionsDemo`, and `JavaTuplesDemo`. These Java 17-compatible examples are shared with the later modules.

Run:

```powershell
.\gradlew.bat :java17:run
```

Java 17 output is the comparison baseline for the Java 21 and Java 25 modules. See the [project README](../README.md) for implementation trade-offs and concurrency alternatives.
