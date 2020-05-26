package com.radcortez.flyway.test.junit;

import com.radcortez.flyway.test.annotation.FlywayTest;
import com.radcortez.flyway.test.annotation.DataSource;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
@FlywayTest(value = @DataSource(url = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"), clean = false)
class H2NoCleanTest {
    @Test
    @Order(1)
    void migration() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            final Result<Record> users = DSL.using(conn).select().from("Users").fetch();
            assertEquals(1, users.size());
        }
    }

    @Test
    @Order(2)
    void insert() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            final DSLContext jooq = DSL.using(conn);
            jooq.query("insert into users values ('97b8435f-3f02-4bd3-88cb-c973e903263e', 'Sasuke', 'Uchiha', 17)")
                .execute();

            final Result<Record> users = jooq.select().from("Users").fetch();
            assertEquals(2, users.size());
        }
    }

    @Test
    @Order(3)
    void cleanState() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            final Result<Record> users = DSL.using(conn).select().from("Users").fetch();
            assertEquals(2, users.size());
        }
    }
}
