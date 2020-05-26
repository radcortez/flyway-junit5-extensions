package com.radcortez.flyway.test.junit;

import com.radcortez.flyway.test.annotation.FlywayTest;
import com.radcortez.flyway.test.annotation.DataSource;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FlywayTest(value = @DataSource(url = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"))
class H2LocationTest {
    @Test
    void migration() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            final Result<Record> users = DSL.using(conn).select().from("Users").fetch();
            assertEquals(2, users.size());
        }
    }

    @Test
    @FlywayTest(additionalLocations = "db/additionalLocation")
    void additionalLocations() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            final Result<Record> users = DSL.using(conn).select().from("Users").fetch();
            assertEquals(3, users.size());
        }
    }
}
