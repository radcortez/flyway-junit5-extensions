package com.radcortez.flyway.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@FlywayTest(value = @DataSource(url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"))
public @interface H2 {

}
