# Java 21 Foundation Examples

This module is a tested quick reference to core Java programming, compiled with the Java 21 release. It shares the same topic coverage as the Java 17 edition while demonstrating APIs available in Java 21.

## Examples

- Primitive values, wrapper types, boxing, conversions, overflow, and operators
- Conditionals, classic and expression-style switches, loops, recursion, methods, overloads, and varargs
- Arrays: linear and binary search, quicksort, merging, and equivalent `Arrays` and stream operations
- `Comparable`, `Comparator`, functional interfaces, lambdas, and custom callbacks
- Strings, text processing, and simple alphabetic/alphanumeric encodings
- `java.time` dates and durations; NIO paths, text/binary files, and resource cleanup
- Null handling, exceptions, and annotations
- Concurrency with platform and virtual threads, virtual-thread-per-task executors, futures, `CompletableFuture`, latches, atomics, locks, and semaphores

## Java 21 Focus

`FoundationAsync` demonstrates `Thread.startVirtualThread`, `Executors.newVirtualThreadPerTaskExecutor`, and automatic executor shutdown with try-with-resources.

## Build and Test

From the repository root:

```powershell
mvn -pl foundations_21 test
```
