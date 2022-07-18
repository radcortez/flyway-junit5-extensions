package com.radcortez.flyway.jooq;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

public class UserRepository {
    public Optional<User> find(final String id) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")) {
            DSLContext create = DSL.using(conn);
            Result<Record> result = create.select().from("USERS").where("ID = :id", id).fetch();
            if (result.size() == 1) {
                Record record = result.get(0);
                User user = User.builder()
                                .id(record.get("ID", String.class))
                                .firstName(record.get("FIRST_NAME", String.class))
                                .lastName(record.get("LAST_NAME", String.class))
                                .age(record.get("AGE", Integer.class))
                                .build();
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
