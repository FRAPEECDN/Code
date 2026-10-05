# Foundation I/O and Networking

A runnable Java learning project for file I/O, serialization, and client/server communication. It follows the `foundation-gradle-template` multi-project layout and runs shared examples with Java 17, 21, and 25.

Everything runs locally. File examples use temporary directories; network servers bind only to loopback on ephemeral ports. No Docker, database, public service, or fixed port is required.

## Learning Path

1. Trace bytes and characters through classic `java.io` streams, readers, and writers.
2. Use NIO.2 `Path` and `Files`, and compare their convenient blocking calls with an asynchronous file-channel write.
3. Follow one immutable message through Java object serialization and Jackson JSON.
4. Trace a client request and server response over TCP, UDP, HTTP/1.1, and WebSocket.
5. Compare Java 21 virtual threads with Java 25 preview structured concurrency.

## Requirements

- A JDK 17, 21, or 25 for its matching module. Gradle's Foojay resolver may provision missing toolchains when network access is available.
- Network access to Maven Central on the first build for Jackson, Java-WebSocket, and JUnit.

## Build and Run

Run from this directory in PowerShell:

```powershell
.\gradlew.bat build
.\gradlew.bat :java17:run
.\gradlew.bat :java21:run
.\gradlew.bat :java25:run
```

`build` runs the shared tests with all three toolchains and each version-specific test. Java 25 preview flags are scoped to `java25`; that module may print the JDK's expected preview-feature notice.

Run one toolchain's tests with:

```powershell
.\gradlew.bat :java17:test
.\gradlew.bat :java21:test
.\gradlew.bat :java25:test
```

## Project Structure

```text
shared/src/main/java/foundation/io/
  App.java                         Shared demonstration entry point
  files/FileIoExamples.java        Streams, readers, Path, Files, async file channel
  serialization/                   Java object serialization and Jackson JSON
  network/NetworkExamples.java      TCP, UDP, HTTP/1.1, and WebSocket client/server pairs
shared/src/test/java/               Shared I/O, serialization, and network integration tests
java17/                             Java 17 baseline application
java21/                             Virtual-thread TCP example and test
java25/                             Structured-concurrency preview example and test
gradle/libs.versions.toml           Central dependency versions
```

## File I/O: Bytes, Characters, and Paths

`FileIoExamples.run()` writes and reads four temporary files to make the APIs comparable.

| Example | What the code sends to storage | What the read side returns | Key difference |
| --- | --- | --- | --- |
| `OutputStream` / `InputStream` | UTF-8 encoded bytes | Bytes, decoded explicitly as UTF-8 | Streams do not know whether bytes represent text, images, or another format |
| `Writer` / `BufferedReader` | Characters encoded as UTF-8 | Lines without their line terminators | Readers and writers handle character conversion and text-oriented operations |
| `Path` / `Files` | Whole-file text through `writeString` | Whole-file text through `readString` | NIO.2 offers concise path-based operations and directory traversal |
| `AsynchronousFileChannel` | A `ByteBuffer` at a file position | The example reads back after the write completes | The API reports completion through a `Future`; this code calls `get()` to wait |

The stream example first encodes text into a `byte[]`, writes those bytes, reads them into a byte accumulator, and then decodes them. The reader example writes two lines; `BufferedReader.lines()` removes the line separators, so the report joins the lines with ` | ` to make the result easy to see. The NIO.2 example uses a `Path` as the file identity and `Files` as the operations API. Finally, the asynchronous-channel example starts a write and waits on its future before reading the result. In a real application, the caller could do other work before waiting or use a completion handler.

**Modern does not automatically mean non-blocking.** Most `Files` methods block until their operation completes. `java.nio.file` is the modern file API; asynchronous file channels are a separate NIO facility. Non-blocking network I/O is typically built with `java.nio.channels` and selectors. This project instead teaches blocking sockets first, then shows how newer concurrency models make blocking I/O scale differently.

All paths are created under a fresh temporary directory. A `finally` block walks that directory in reverse path order and deletes its files before deleting the directory itself.

## Serialization: Object Graph Versus JSON

`SerializationExamples.run()` starts with one immutable `ProjectMessage` instance and creates two independent representations.

| | Java object serialization | Jackson JSON |
| --- | --- | --- |
| Write API | `ObjectOutputStream.writeObject` | `ObjectMapper.writeValueAsString` |
| Representation | Java-specific binary stream with class/object-graph metadata | Text with named properties such as `projectName` and `priority` |
| Read API | `ObjectInputStream.readObject` reconstructs a compatible Java class | Jackson maps JSON properties to the annotated constructor |
| Best teaching point | Convenient for Java object graphs, but tightly coupled to Java classes and versions | Readable and usable across languages when the schema and compatibility rules are managed |
| Main caution | Never deserialize arbitrary untrusted object streams | JSON still needs validation, schema/version policy, and limits on accepted input |

The object-stream path writes bytes into a `ByteArrayOutputStream`, then reads them through an `ObjectInputStream`. Before `readObject()`, it installs an allow-list filter for `ProjectMessage` and required `java.base` classes; the final `!*` rejects other classes. `serialVersionUID` makes Java's class-version compatibility check explicit. Filters reduce the available deserialization surface, but native Java deserialization remains a risky choice for untrusted input.

The JSON path asks Jackson to inspect JavaBean getters and write named fields. On read, `@JsonCreator` and `@JsonProperty` specify exactly how those names become constructor arguments. JSON does not preserve Java class metadata or object-reference identity, but the text is inspectable and can be consumed by other languages. For a long-lived or cross-service contract, use explicit schema/versioning; Protocol Buffers are another option when a strongly defined compact binary schema is preferred.

The test compares the original and restored values, not the two encoded forms. Their byte layouts are intentionally different and are not interchangeable.

## Client/Server Communication

`NetworkExamples.run()` starts four local servers in sequence. For each one, the client sends the literal request shown below, the server echoes it, and the client returns the response for printing. Each server binds to loopback and port `0`; the operating system chooses an available port. Five-second timeouts keep a failed local exchange from waiting indefinitely.

### TCP: A Reliable Byte Stream

1. The server creates a `ServerSocket`, binding it to loopback and the chosen port. `accept()` waits for a client.
2. The client creates a `Socket` to that address. The TCP connection setup is performed by the operating system; when construction returns, both sides can read and write the connected stream.
3. The client encodes `tcp request` as UTF-8, writes it, appends a newline, and flushes the writer.
4. The server reads until that newline, echoes the line plus a newline, and flushes its writer.
5. The client reads one line as the response, and both sockets close.

TCP preserves byte order and retransmits lost data, but it does **not** preserve calls to `write()` as message boundaries. The newline is an application-level framing rule chosen by this example. A different application might use a fixed-size header, a length prefix, or another framing format.

### UDP: Independent Datagrams

1. The server binds a `DatagramSocket`; it does not call `accept()` and there is no connection handshake.
2. The client places the request bytes, destination address, and destination port into one `DatagramPacket` and sends it.
3. The server's `receive()` fills a packet with the payload and records the sender address and actual payload length.
4. The server sends a new packet containing those bytes back to the recorded sender. The client receives and decodes that one response datagram.

Unlike TCP, datagram boundaries are preserved, but UDP does not guarantee delivery, ordering, or duplicate suppression. The socket timeout only limits how long this demo waits; it does not make UDP reliable.

### HTTP/1.1: A Framed Request/Response

The server registers `/echo` and starts listening. The client explicitly selects HTTP/1.1, builds a `POST` request, sets `Content-Type`, and supplies `http/1.1 request` as its body. Conceptually, the exchange is:

```http
POST /echo HTTP/1.1
Content-Type: text/plain; charset=utf-8

http/1.1 request
```

The JDK `HttpClient` handles HTTP framing and the underlying TCP connection. The server handler reads the complete request body, then writes a `200` status, a response content type, a content length, and the same bytes as the body. The client parses the response and checks both the status and protocol version before returning its body. Unlike the raw TCP example, HTTP defines methods, paths, headers, body framing, and response status semantics.

### WebSocket: Upgrade, Then Persistent Messages

1. The client connects to a `ws://` URI. The WebSocket library sends the HTTP opening handshake requesting an upgrade.
2. After the server accepts the upgrade, the client's `onOpen` callback sends `websocket request` as a text message.
3. The server's `onMessage` callback receives one complete WebSocket message and sends the same message back.
4. The client's `onMessage` callback stores the response and releases a latch; the calling thread then closes the client and server.

HTTP request/response normally completes one exchange at a time. WebSocket keeps the upgraded connection open and allows either peer to send framed messages independently. The handshake begins as HTTP, but the later message flow is not a series of HTTP requests.

These are protocol-mechanics examples, not production servers. They omit TLS, authentication, input-size limits, backpressure, retry policy, and robust service lifecycle management.

## Java Version Enhancements

### Java 17: Baseline APIs

The shared examples use records for reports, `java.io`, NIO.2, object streams, the JDK `HttpClient`, sockets, and the WebSocket library. Run them with:

```powershell
.\gradlew.bat :java17:run
```

### Java 21: Virtual Threads for Blocking I/O

`VirtualThreadTcpExample` repeats the accept/read/echo/write pattern with a virtual-thread-per-task executor. The caller thread creates the client connection while the virtual thread blocks in `accept()` and then handles the request. The test checks `Thread.isVirtual()` inside the server handler.

Virtual threads make it inexpensive to have many concurrent tasks that spend time blocked on I/O. They do not make `Socket` non-blocking and do not alter TCP message framing.

```powershell
.\gradlew.bat :java21:run
```

### Java 25: Structured Concurrency Preview

`StructuredConcurrencyExample` forks two independent operations: one runs the NIO.2 file example and one produces the JSON representation. The owner calls `join()` before retrieving either result. The all-success joiner cancels unfinished siblings and reports a failure if a subtask fails; closing the scope ensures child work does not outlive the operation that started it.

`StructuredTaskScope` is a Java 25 preview API and may change in a later JDK. Only the `java25` module opts into `--enable-preview` for compilation, Javadoc, tests, and application execution.

```powershell
.\gradlew.bat :java25:run
```

## Tests

Shared JUnit tests assert the file contents, both serialization round trips, and each of the four client/server exchanges. Java 21 has an additional virtual-thread assertion; Java 25 verifies both joined task results. The tests use local temporary files, loopback sockets, ephemeral ports, and bounded timeouts. They do not connect to external services.

## Dependencies

- Jackson Databind maps the immutable message to/from JSON.
- Java-WebSocket supplies a matching WebSocket server and client. The JDK provides a WebSocket client API, but no matching built-in server API for this example.
- JUnit 5 runs shared and version-specific tests.
- Dependency versions are centralized in `gradle/libs.versions.toml`.
