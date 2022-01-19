package com.radcortez.flyway.quarkus;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.radcortez.flyway.test.junit.DataSourceInfo;
import com.radcortez.flyway.test.junit.DataSourceProvider;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtensionContext;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;

@DisabledOnOs(OS.WINDOWS)
@QuarkusTest
@TestProfile(UserRepositoryDevServicesTest.DevServicesTestProfile.class)
@FlywayTest(@DataSource(UserRepositoryDevServicesTest.QuarkusDataSourceProvider.class))
public class UserRepositoryDevServicesTest {
    @Test
    void find() {
        given().get("/users/{id}", "3df5eeff-f93d-4036-b1aa-9e96a7a8820d")
               .then()
               .statusCode(OK.getStatusCode());
    }

    public static class DevServicesTestProfile implements QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "dev";
        }
    }

    public static class QuarkusDataSourceProvider implements DataSourceProvider {
        @Override
        public DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext) {
            // Quarkus Dev Services rewrite the Dev Service datasource in the configuration
            Config config = ConfigProvider.getConfig();
            String url = config.getValue("quarkus.datasource.jdbc.url", String.class);
            String username = config.getValue("quarkus.datasource.username", String.class);
            String password = config.getValue("quarkus.datasource.password", String.class);
            return DataSourceInfo.config(url, username, password);
        }
    }
}
