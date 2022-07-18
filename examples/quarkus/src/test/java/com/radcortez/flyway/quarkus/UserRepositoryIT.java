package com.radcortez.flyway.quarkus;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.radcortez.flyway.test.junit.DataSourceInfo;
import com.radcortez.flyway.test.junit.DataSourceProvider;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.extension.ExtensionContext;

@FlywayTest(@DataSource(UserRepositoryIT.QuarkusDataSourceProvider.class))
@QuarkusIntegrationTest
public class UserRepositoryIT extends UserRepositoryTest {
    public static class QuarkusDataSourceProvider implements DataSourceProvider {
        @Override
        public DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext) {
            return DataSourceInfo.config("jdbc:postgresql://localhost:5432/database", "database", "password");
        }
    }
}
