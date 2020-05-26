package com.radcortez.flyway.spring;

import com.radcortez.flyway.test.junit.DataSourceInfo;
import com.radcortez.flyway.test.junit.DataSourceProvider;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class SpringDataSourceProvider implements DataSourceProvider {
    @Override
    public DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext) {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);
        final String url = applicationContext.getEnvironment().getProperty("spring.datasource.url");
        final String username = applicationContext.getEnvironment().getProperty("spring.datasource.username");
        final String password = applicationContext.getEnvironment().getProperty("spring.datasource.password");

        return DataSourceInfo.config(url, username, password);
    }
}
