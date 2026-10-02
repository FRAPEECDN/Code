# Java Foundation Examples

This repository contains three independent, tested editions of a Java foundations reference project. Each edition uses the same topic areas while targeting a different Java release, so examples can be compared across Java 17, 21, and 25.

## Modules

| Module | Java release | Version-specific examples |
| --- | --- | --- |
| [`foundations_17`](foundations_17/README.MD) | 17 | Core language and library examples; concurrency uses platform threads and Java 17 executor APIs. |
| [`foundations_21`](foundations_21/README.MD) | 21 | Adds virtual threads and virtual-thread-per-task executors to the concurrency examples. |
| [`foundations_25`](foundations_25/README.MD) | 25 | Adds scoped values and a type-pattern switch, with structured concurrency and primitive type patterns available as opt-in preview examples. |

## Foundation Topics

Across the modules, examples cover:

- Primitive and wrapper types, boxing, conversions, operators, and null handling
- Branching, switch statements/expressions, loops, recursion, methods, overloads, and varargs
- Arrays, searching, sorting, merging, and standard array/stream utilities
- Comparisons, functional interfaces, lambdas, and callback patterns
- Strings and simple text encodings
- Dates and durations with `java.time`
- File paths, text and binary I/O, and resource management
- Exceptions, annotations, and concurrency

The Java 25 edition also includes stable type-pattern and scoped-value examples. Preview features are separated from the standard build so they do not affect the ordinary reactor test run.

## Requirements

- Apache Maven 3.6.3 or newer
- JDK 25 or newer to build the full reactor
- JDK 25 specifically to compile and run the Java 25 preview examples

Individual modules require a JDK at least as new as their configured release. The Maven compiler uses each module's `--release` setting.

## Build and Test

Run compile and tests for all three modules from this directory:

```powershell
mvn test
```

To build and test one edition:

```powershell
mvn -pl foundations_17 test
mvn -pl foundations_21 test
mvn -pl foundations_25 test
```

To run Java 25 preview examples, use the command documented in the [Java 25 module README](foundations_25/README.MD). The root `pom.xml` is the Maven parent and reactor aggregator for the three modules.