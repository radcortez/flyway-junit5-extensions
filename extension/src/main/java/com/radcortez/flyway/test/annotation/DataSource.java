package com.radcortez.flyway.test.annotation;

import com.radcortez.flyway.test.junit.DataSourceInfo;
import com.radcortez.flyway.test.junit.DataSourceProvider;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    Class<? extends DataSourceProvider> value() default DEFAULT.class;

    String url() default "";

    String username() default "";

    String password() default "";

    class DEFAULT implements DataSourceProvider {
        @Override
        public DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext) {
            return DataSourceInfo.empty();
        }
    }
}
