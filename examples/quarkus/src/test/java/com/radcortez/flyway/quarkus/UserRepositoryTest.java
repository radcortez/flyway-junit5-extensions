package com.radcortez.flyway.quarkus;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@FlywayTest(@DataSource(QuarkusDataSourceProvider.class))
class UserRepositoryTest {
    @Inject
    UserRepository userRepository;

    @Test
    void find() {
        final Optional<User> user = userRepository.find("3df5eeff-f93d-4036-b1aa-9e96a7a8820d");
        assertTrue(user.isPresent());
    }

    @Test
    void create() {

    }

    @Test
    void update() {

    }

    @Test
    void delete() {

    }

    @Test
    void findByFirstName() {

    }
}
