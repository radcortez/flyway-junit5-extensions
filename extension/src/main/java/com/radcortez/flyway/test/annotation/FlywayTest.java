package com.radcortez.flyway.test.annotation;

import com.radcortez.flyway.test.junit.FlywayExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(FlywayExtension.class)
public @interface FlywayTest {
    DataSource value() default @DataSource();

    String[] additionalLocations() default {};

    boolean clean() default true;
}
