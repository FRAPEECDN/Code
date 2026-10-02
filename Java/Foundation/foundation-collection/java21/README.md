# Java 21 Collections

This module runs the shared Java 17-compatible collection walkthrough, then uses `shared-java21/src/main/java/com/fp/coding/SequencedCollectionsDemo.java` to demonstrate Java 21's sequenced collections (JEP 431):

- `SequencedCollection`: first/last access, adding/removing at an end, and a reversed view.
- `SequencedSet`: moving an existing element to the front while retaining set uniqueness.
- `SequencedMap`: `putFirst`, `putLast`, first/last entries, reversed keys, and removing the last entry.

Sequenced interfaces provide explicit encounter-order operations; the backing examples are not thread-safe. See the source comments for concurrent alternatives and their ordering trade-offs.

Java 25 compiles and reuses this same helper. It does not duplicate the Java 21 sequenced-collection implementation.

Run:

```powershell
.\gradlew.bat :java21:run
```

See the [project README](../README.md) for the shared CRUD, search, stream, and library examples.
