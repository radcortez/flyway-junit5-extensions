package com.radcortez.flyway.test.junit;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(OrderAnnotation.class)
@FlywayTest(value = @DataSource(url = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"))
class H2NestedInheritance {
    @Test
    @Order(1)
    void migration() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            Result<Record> users = DSL.using(conn).select().from("Users").fetch();
            assertEquals(1, users.size());
        }
    }

    @Test
    @Order(2)
    void insert() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            DSLContext jooq = DSL.using(conn);
            jooq.query("insert into users values ('97b8435f-3f02-4bd3-88cb-c973e903263e', 'Sasuke', 'Uchiha', 17)")
                .execute();

            Result<Record> users = jooq.select().from("Users").fetch();
            assertEquals(2, users.size());
        }
    }

    @Test
    @Order(3)
    void cleanState() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            Result<Record> users = DSL.using(conn).select().from("Users").fetch();
            assertEquals(1, users.size());
        }
    }

    @Order(1)
    @Nested
    class NestedMigration {
        @Test
        void migration() throws Exception {
            try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
                Result<Record> users = DSL.using(conn).select().from("Users").fetch();
                assertEquals(1, users.size());
            }
        }
    }

    @Order(2)
    @Nested
    @FlywayTest(clean = false)
    // This will not clean the state, so the next Nested test will see the additional record
    class NestedInsert {
        @Test
        void insert() throws Exception {
            System.out.println("NestedInsert.insert");
            try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
                DSLContext jooq = DSL.using(conn);
                jooq.query("insert into users values ('97b8435f-3f02-4bd3-88cb-c973e903263e', 'Sasuke', 'Uchiha', 17)")
                    .execute();

                Result<Record> users = jooq.select().from("Users").fetch();
                assertEquals(2, users.size());
            }
        }

        @Test
        void dirty() throws Exception {
            System.out.println("NestedInsert.dirty");
            try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
                Result<Record> users = DSL.using(conn).select().from("Users").fetch();
                assertEquals(2, users.size());
            }
        }
    }

    @Order(3)
    @Nested
    // Now clean, because it picks @FlywayTest from enclosing class
    class NestedClean {
        @Test
        void clean() throws Exception {
            try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
                Result<Record> users = DSL.using(conn).select().from("Users").fetch();
                assertEquals(1, users.size());
            }
        }
    }
}
