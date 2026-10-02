# Java 25 Foundation Examples

This module is a tested quick reference to core Java programming, compiled with the Java 25 release. It shares the core topic coverage of the Java 17 and 21 editions and adds stable Java 25 examples plus an opt-in area for preview features.

## Examples

- Primitive values, wrapper types, boxing, conversions, overflow, and operators
- Conditionals, classic and expression-style switches, loops, recursion, methods, overloads, and varargs
- Type-pattern switches, including handling `null`
- Arrays: linear and binary search, quicksort, merging, and equivalent `Arrays` and stream operations
- `Comparable`, `Comparator`, functional interfaces, lambdas, and custom callbacks
- Strings, text processing, and simple alphabetic/alphanumeric encodings
- `java.time` dates and durations; NIO paths, text/binary files, and resource cleanup
- Null handling, exceptions, and annotations
- Platform/virtual threads, executors, futures, `CompletableFuture`, scoped values, latches, atomics, locks, and semaphores

## Java 25 Preview Examples

The `java25-preview` Maven profile adds examples for structured concurrency and primitive type patterns. These are preview features in Java 25 and are kept outside the standard source set.

From the repository root, run the complete Java 25 suite with preview enabled:

```powershell
mvn -pl foundations_25 -Pjava25-preview test
```

Use JDK 25 for this profile. Preview build outputs are isolated under `target/java25-preview`; a normal `mvn test` does not compile or run the preview examples.

## Standard Build and Test

```powershell
mvn -pl foundations_25 test
```
