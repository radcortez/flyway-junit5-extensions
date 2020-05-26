package com.radcortez.flyway.test.junit;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface DataSourceProvider {
    DataSourceInfo getDatasourceInfo(final ExtensionContext extensionContext);
}
