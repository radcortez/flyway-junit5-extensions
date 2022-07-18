package com.radcortez.flyway.jooq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"})
public class User {
    private String id;

    private String firstName;

    private String lastName;

    private Integer age;
}
