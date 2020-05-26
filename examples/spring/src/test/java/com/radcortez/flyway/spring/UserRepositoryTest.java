package com.radcortez.flyway.spring;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@FlywayTest(@DataSource(SpringDataSourceProvider.class))
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void find() {
        final Optional<User> user = userRepository.findById("3df5eeff-f93d-4036-b1aa-9e96a7a8820d");
        assertTrue(user.isPresent());
    }
}
