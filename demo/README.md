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
- JUnit (3.8.1) - Testing framework

## Building the Project

To build the project, run:

```bash
mvn clean install
```

This will:
1. Generate the lexer from JFlex specifications
2. Generate the parser from CUP specifications
3. Compile all Java sources
4. Run tests
5. Create the final JAR file

## Usage

After building, you can run the parser with:

```bash
java -jar target/imperativeLangParser-1.0-SNAPSHOT.jar <input-file>
```

## Development

The project uses:
- Maven for dependency management and build automation
- JFlex for lexical analysis
- Java CUP for parser generation

## License

This project is part of a compiler course implementation. 