# Imperative Language Parser

A Java-based compiler project for parsing and processing imperative programming languages.

## Overview

This project implements a compiler/parser for imperative programming languages using JFlex for lexical analysis and Java CUP for parsing. It's designed to demonstrate the fundamentals of compiler construction and language processing.

## Project Structure

```
.
├── src/
│   ├── main/      # Main source code
│   └── test/      # Test files
├── examples/      # Example input files
├── lib/          # Additional libraries
├── output/       # Generated output files
└── target/       # Compiled files
```

## Prerequisites

- Java JDK 8 or higher
- Maven 3.x

## Dependencies

- JFlex (1.8.2) - Lexical analyzer generator
- Java CUP (11b-20160615) - Parser generator
- JUnit (4.13.2) - Testing framework

## Development Build

To build the project for development, run:

```bash
mvn clean install
```

This will:
1. Generate the lexer from JFlex specifications
2. Generate the parser from CUP specifications
3. Compile all Java sources
4. Run tests
5. Create the final JAR file

## Production Deployment

To prepare the project for production deployment:

1. Build the production JAR:

```bash
mvn clean package
```

This will create a self-contained executable JAR in the `target` directory.

2. Run the production JAR:

```bash
java -jar target/imperativeLangParser-1.0-SNAPSHOT.jar <input-file>
```

### Production Considerations

- The production JAR includes all necessary dependencies
- JVM memory can be configured using `-Xmx` and `-Xms` flags if needed
- For logging in production, configure `log4j` properties in the resources directory
- Monitor memory usage and performance in production environment

## Usage Examples

1. Basic usage:

```bash
java -jar target/imperativeLangParser-1.0-SNAPSHOT.jar examples/basic.imp
```

2. With increased memory:

```bash
java -Xmx2g -jar target/imperativeLangParser-1.0-SNAPSHOT.jar examples/large.imp
```

## Development

The project uses:
- Maven for dependency management and build automation
- JFlex for lexical analysis
- Java CUP for parser generation

### Development Guidelines

1. Follow Java coding standards
2. Add unit tests for new features
3. Update documentation for API changes
4. Use meaningful commit messages

## License

This project is part of a compiler course implementation. 