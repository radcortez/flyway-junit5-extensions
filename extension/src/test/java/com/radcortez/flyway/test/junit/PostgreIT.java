package com.radcortez.flyway.test.junit;

import com.radcortez.flyway.test.annotation.FlywayTest;
import com.radcortez.flyway.test.annotation.DataSource;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@FlywayTest(value = @DataSource(PostgreIT.TestContainersDataSourceProvider.class))
class PostgreIT {
    @Container
    private static final JdbcDatabaseContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer();

    @Test
    void migrate() throws Exception {
        try (Connection conn = DriverManager.getConnection(POSTGRE_SQL_CONTAINER.getJdbcUrl(),
                                                           POSTGRE_SQL_CONTAINER.getUsername(),
                                                           POSTGRE_SQL_CONTAINER.getPassword())) {
            final Result<Record> users = DSL.using(conn).select().from("Users").fetch();
            assertEquals(1, users.size());
        }
    }

    static class TestContainersDataSourceProvider implements DataSourceProvider {
        @Override
        public DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext) {
            try {
                final Class<?> testContainers =
                    Class.forName("org.testcontainers.junit.jupiter.TestcontainersExtension");

                final Field namespace = testContainers.getDeclaredField("NAMESPACE");
                namespace.setAccessible(true);
                final ExtensionContext.Store store =
                    extensionContext.getStore((Namespace) namespace.get(Namespace.class));
                final Object storeAdapter =
                    store.get(extensionContext.getTestClass().get().getName() + "." + "POSTGRE_SQL_CONTAINER");

                final Field container = storeAdapter.getClass().getDeclaredField("container");
                container.setAccessible(true);
                final JdbcDatabaseContainer database = (JdbcDatabaseContainer) container.get(storeAdapter);

                return DataSourceInfo.config(database.getJdbcUrl(), database.getUsername(), database.getPassword());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
