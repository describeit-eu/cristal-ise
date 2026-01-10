### Project Overview
This project is a Vert.x-based application called **Cristalise**, primarily written in **Groovy** with some Java components. It uses **Dagger 2** for dependency injection, **Liquibase** for database schema management, and a mix of **JUnit 5** and **Spock** for testing.

### Build and Configuration
The project uses Gradle (Kotlin DSL).

- **Clean and Build**:
  ```bash
  ./gradlew clean assemble
  ```
- **Run Application**:
  ```bash
  ./gradlew run
  ```
- **Configuration**:
  - Main development configuration is located in `conf/dev/config.json`.
  - Test configuration is in `kernel/src/test/conf/config.json`.
  - System property `vertx-config-path` is used to point to the configuration file (often set in `AbstractRepositoryIT`).

### Testing Guidelines

#### Running Tests
- **All tests**:
  ```bash
  ./gradlew test
  ```
- **Specific test class**:
  ```bash
  ./gradlew :kernel:test --tests "eu.describeit.cristalise.kernel.SimpleDocTest"
  ```

#### Adding New Tests
1. **Unit Tests**: Use Spock (`Specification`) or JUnit 5 and place them in `kernel/src/test/groovy/`.
2. **Integration Tests (IT)**:
   - Integration tests often require a database. The project uses **Testcontainers** with PostgreSQL.
   - Extend `eu.describeit.cristalise.kernel.persistency.repository.AbstractRepositoryIT` to get automatic database setup and Liquibase migration.
   - Annotate with `@Testcontainers(disabledWithoutDocker = true)` and `@TestInstance(Lifecycle.PER_CLASS)`.

#### Example Test (Spock)
```groovy
package eu.describeit.cristalise.kernel

import spock.lang.Specification

class MyNewTest extends Specification {
    def "should verify basic logic"() {
        given:
        def value = 10

        expect:
        value * 2 == 20
    }
}
```

### Development Information

#### Code Style & Patterns
- **Groovy Usage**: Use `@CompileStatic` for better performance and type safety unless dynamic features are specifically needed.
- **Vert.x Data Objects**: Use `@DataObject`, `@JsonGen`, `@RowMapped`, and `@ParametersMapped` for domain objects to leverage Vert.x code generation for JSON and SQL mapping.
- **Asynchronous Code**: Prefer `io.vertx.core.Future` for asynchronous operations.
- **Dependency Injection**: Use Dagger 2. Components are defined in `eu.describeit.cristalise.kernel.dagger`.

#### Database Migrations
- Liquibase changelogs are located in `kernel/src/main/resources/liquibase/changelog/`.
- Test data is managed via separate changelogs in `kernel/src/test/resources/liquibase/changelog/`.
- Always update `changelog-master.yaml` when adding new migrations.
