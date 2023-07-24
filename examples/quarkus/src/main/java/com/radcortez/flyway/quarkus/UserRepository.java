package com.radcortez.flyway.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class UserRepository implements PanacheRepositoryBase<User, String> {
    public Optional<User> find(final String id) {
        return Optional.ofNullable(findById(id));
    }

    public Optional<User> create(final User user) {
        persist(user);
        return Optional.of(user);
    }

    public Optional<User> update(final String id, final User userUpdate) {
        return find(id).map(user -> user.toUser(userUpdate));
    }

    public Optional<User> delete(final String id) {
        return find(id).map(user -> {
            delete(user);
            return user;
        });
    }

    public PanacheQuery<User> findByFirstName(final String firstName) {
        return find("firstName", firstName);
    }
}
