# Spring Boot gRPC User Service

This is a small learning project for understanding:

- Spring Boot configuration, beans, and annotations
- Building a gRPC server and client
- Defining services and messages with Protocol Buffers
- Generating Java classes from a `.proto` file
- Calling RPC endpoints and handling gRPC status errors

User data is stored in memory, so it is cleared whenever the server restarts.

## Requirements

- Java 17
- Git
- Internet access for the first dependency download

You do **not** need to install Maven globally. Always use the Maven Wrapper included in this repository:

```bash
./mvnw
```

The wrapper downloads and uses the Maven version configured for this project without affecting other Java projects on your machine.

## Implemented RPCs

The service definition is in `src/main/proto/user.proto`.

- `CreateUser` — creates a user and assigns an ID
- `GetUser` — retrieves a user by ID
- `ListUsers` — returns all users
- `UpdateUser` — updates a user's name or email
- `DeleteUser` — deletes a user by ID

The gRPC server listens on `localhost:9090`.

## Get Started

Clone the repository and enter the project directory:

```bash
git clone <repository-url>
cd server
```

Generate the protobuf classes and compile the project:

```bash
./mvnw clean compile
```

Generated Java files are written under:

```text
target/generated-sources/protobuf/java
target/generated-sources/protobuf/grpc-java
```

Do not edit these generated files directly.

## Start the Server

```bash
./mvnw spring-boot:run
```

Keep this terminal running.

## Run the Client Tests

Open another terminal in the project directory while the server is running:

```bash
./mvnw exec:java -Dexec.mainClass="grpc.server.client.UserClient"
```

The client exercises all implemented RPCs, verifies update and deletion behavior, and checks validation error handling.

To run the Maven test suite:

```bash
./mvnw test
```

## Main Project Files

- `src/main/proto/user.proto` — gRPC service and message definitions
- `src/main/java/grpc/server/service/UserService.java` — server implementation
- `src/main/java/grpc/server/client/UserClient.java` — client and RPC test flow
- `src/main/resources/application.properties` — application and gRPC configuration
