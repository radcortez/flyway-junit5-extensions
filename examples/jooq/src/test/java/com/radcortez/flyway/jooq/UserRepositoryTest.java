package com.radcortez.flyway.jooq;

import com.radcortez.flyway.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlywayTest
class UserRepositoryTest {
    @Test
    void find() throws Exception {
        Optional<User> user = new UserRepository().find("3df5eeff-f93d-4036-b1aa-9e96a7a8820d");
        assertTrue(user.isPresent());
        assertEquals("Naruto", user.get().getFirstName());
    }
}
