package com.radcortez.flyway.quarkus;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import com.radcortez.flyway.test.junit.DataSourceInfo;
import com.radcortez.flyway.test.junit.DataSourceProvider;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Header;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@FlywayTest(@DataSource(UserRepositoryTest.QuarkusDataSourceProvider.class))
class UserRepositoryTest {
    @BeforeAll
    static void beforeAll() {
        RestAssured.filters(
            (requestSpec, responseSpec, ctx) -> {
                requestSpec.header(new Header(CONTENT_TYPE, APPLICATION_JSON));
                requestSpec.header(new Header(ACCEPT, APPLICATION_JSON));
                return ctx.next(requestSpec, responseSpec);
            },
            new RequestLoggingFilter(),
            new ResponseLoggingFilter());
    }

    @Test
    void find() {
        given().get("/users/{id}", "3df5eeff-f93d-4036-b1aa-9e96a7a8820d")
               .then()
               .statusCode(OK.getStatusCode());
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

    public static class QuarkusDataSourceProvider implements DataSourceProvider {
        @Override
        public DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext) {
            return DataSourceInfo.config(ConfigProvider.getConfig().getValue("quarkus.datasource.jdbc.url", String.class));
        }
    }
}
