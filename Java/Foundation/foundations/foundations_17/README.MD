# Java 17 Foundation Examples

This module is a tested quick reference to core Java programming, compiled with the Java 17 release. It provides small examples intended for learning and comparison, not a production library.

## Examples

- Primitive values, wrapper types, boxing, conversions, overflow, and operators
- Conditionals, classic and expression-style switches, loops, recursion, methods, overloads, and varargs
- Arrays: linear and binary search, quicksort, merging, and equivalent `Arrays` and stream operations
- `Comparable`, `Comparator`, functional interfaces, lambdas, and custom callbacks
- Strings, text processing, and simple alphabetic/alphanumeric encodings
- `java.time` dates and durations; NIO paths, text/binary files, and resource cleanup
- Null handling, exceptions, and annotations
- Concurrency with platform threads, executors, futures, `CompletableFuture`, latches, atomics, locks, and semaphores

## Java 17 Focus

The concurrency examples use Java 17 APIs. This edition does not use virtual threads or newer thread-per-task executors.

## Build and Test

From the repository root:

```powershell
mvn -pl foundations_17 test
```
