# Java 25 Collections

Java 25 adds no new collection interfaces beyond Java 21's Sequenced Collections. This module runs the shared Java 17 baseline, reuses the same Java 21 sequenced-collection helper, and then demonstrates Stream Gatherers in Java 25-only source. Gatherers were finalized in JDK 24 (JEP 485); they extend streams and are not a collection API.

- `Gatherers.windowFixed(2)` groups the ordered projects into windows of two.
- `Gatherers.scan(...)` emits a running total of project budgets.

Gatherers extend stream pipelines; these examples are sequential and do not mutate the source projects. See the source comments for guidance on concurrent state and collectors.

Run:

```powershell
.\gradlew.bat :java25:run
```

See the [project README](../README.md) for the collection implementation trade-offs and the Java 17/21/25 progression.
