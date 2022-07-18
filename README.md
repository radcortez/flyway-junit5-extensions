[![License](https://img.shields.io/github/license/smallrye/smallrye-config.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven](https://img.shields.io/maven-central/v/com.radcortez.flyway/flyway-junit5-extension?color=green)](https://search.maven.org/artifact/com.radcortez.flyway/flyway-junit5-extension)
[![Build](https://github.com/radcortez/flyway-junit5-extensions/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/radcortez/flyway-junit5-extensions/actions?query=workflow%3ABuild+branch%3Amain)

# Flyway JUnit 5 Extensions

This extensions allows you to clean / migrate your database using Flyway during testing.

## How to use it?

Add the following dependency to your project:

```xml
<dependency>
  <groupId>com.radcortez.flyway</groupId>
  <artifactId>flyway-junit5-extension</artifactId>
  <version>1.4.0</version>
  <scope>test</scope>
</dependency>
```

---
**NOTE**: 

This project depends on:
- Flyway 8.5.13
- JUnit Jupiter 5.8.2

---

Add the `@FlywayTest` annotation to your test class or method. By default, Flyway will perform the `migrate` action 
before each test execution and the `clean` action after the test. This can be disabled by turning `clean = false` in 
the `@FlywayTest` annotation.  

The only required information in the `@FlywayTest` annotation is the database information that you can supply using 
the inner `@DataSource` annotation. In the `@DataSource` annotation you can specify the `url`, `username` and `password` 
to connect to a running database:

```java
@FlywayTest(@DataSource(url = "jdbc:h2:mem:test"))
class JUnit5Test {

}
```

Or you can implement a `DataSourceProvider` and return a `DataSourceInfo` with the database connection details:

```java
@FlywayTest(@DataSource(JUnit5Test.H2DatasourceProvider.class))
class JUnit5Test {

    static class H2DatasourceProvider implements DataSourceProvider {
        @Override
        public DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext) {
            return DataSourceInfo.config("jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        }
    }
}
```

The `DataSourceProvider` will always take priority over `url`, `username` and `password` in the `@DataSource` 
annotation.

The `@FlywayTest` annotation can also be placed in a method. 

```java
@FlywayTest(additionalLocations = "db/additionalLocation")
void additionalLocations() throws Exception {

}
```

When both the class and the method are annotated, the annotations metadata is merged with the method annotation taking 
priority over the class annotation.

### Conventions

The extension uses the default path to load migration scripts from Flyway, set in `resources/db/migration`.

If you want to add specific database migrations to a particular test, you can place the migration files in 
`resources/db/` plus the fully qualified name of the test as a path. For instance 
`com/radcortez/flyway/test/junit/H2LocationTest`.

Additional migration locations can be defined using the `additionalLocations` metadata in the `@FlywayTest` annotation. 
This will not override the default locations, but just add a location for the migration files.

### Meta Annotations

You can also place the `@FlywayTest` annotation in a meta annotation and then use it in the test class.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@FlywayTest(value = @DataSource(url = "jdbc:h2:mem:test"))
public @interface H2 {

}

@H2
class H2MetaAnnotationTest {
    
}
```

The `@H2` annotation is already available in the extension, but you need to remember to add the H2 dependency to your 
project to be able to use an H2 database:

```xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <version>2.1.214</version>
  <scope>test</scope>
</dependency>
```
