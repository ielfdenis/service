# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 4.0.0 application for reporting functionality, built with Java 17 and Maven. The project uses Spring Web MVC for REST APIs and includes Spring Boot DevTools for development.

## Build System

This project uses Maven with the wrapper included. All Maven commands should use `./mvnw` (or `./mvnw.cmd` on Windows).

### Common Commands

**Build the project:**
```bash
./mvnw clean install
```

**Run the application:**
```bash
./mvnw spring-boot:run
```

**Run all tests:**
```bash
./mvnw test
```

**Run a single test class:**
```bash
./mvnw test -Dtest=ReportingApplicationTests
```

**Run a specific test method:**
```bash
./mvnw test -Dtest=ClassName#methodName
```

**Package the application:**
```bash
./mvnw package
```

## Project Structure

- `src/main/java/com/example/reporting/` - Main application code
  - `ReportingApplication.java` - Spring Boot application entry point
  - `controller/` - REST controllers
  - `service/` - Business logic services
    - `replacer/` - Extensible placeholder replacement strategies
  - `model/` - Domain models and DTOs
  - `util/` - Utility classes
  - `exception/` - Custom exceptions
- `src/main/resources/` - Configuration files and resources
  - `application.properties` - Application configuration
  - `templates/` - PPTX template files (place templates here)
- `src/test/java/com/example/reporting/` - Test code

## Architecture

This is a Spring Boot web application for PPTX template processing using Apache POI. The architecture uses a Strategy pattern for extensibility:

**PPTX Processing Flow:**
1. `PptxReaderService` loads templates from resources and extracts placeholders
2. `PptxModificationService` coordinates replacements using registered replacers
3. `PlaceholderReplacer` implementations (Strategy pattern) handle different types:
   - `TextPlaceholderReplacer` - replaces text placeholders marked with `{{key}}`
   - `ImagePlaceholderReplacer` - replaces image placeholders
   - Additional replacers can be added by implementing the `PlaceholderReplacer` interface
4. `PptxDownloadService` prepares modified presentations for download

**Key Technologies:**
- Spring Boot 4.0.0
- Spring Web MVC for REST endpoints
- Apache POI 5.2.5 for PPTX manipulation
- Lombok for reducing boilerplate
- Spring Boot DevTools for hot reloading during development
- JUnit 5 (Jupiter) for testing

**Extensibility:**
To add new placeholder types, create a new `@Component` implementing `PlaceholderReplacer`. Spring's dependency injection will automatically register it with `PptxModificationService`.

## Development Notes

- The application runs on the default Spring Boot port (8080) unless configured otherwise
- DevTools is enabled, providing automatic restart when classpath files change
- Package structure follows standard Maven conventions: `com.example.reporting`
