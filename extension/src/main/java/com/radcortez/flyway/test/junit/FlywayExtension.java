package com.radcortez.flyway.test.junit;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class FlywayExtension implements TestInstancePostProcessor, BeforeAllCallback, BeforeEachCallback, AfterEachCallback {
    static final Namespace FLYWAY_EXTENSION = Namespace.create(new Object());

    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) {
        context.getTestClass().flatMap(klass -> findAnnotation(klass, FlywayTest.class, true)).ifPresent(
            flywayTest -> {
                Store store = context.getStore(FLYWAY_EXTENSION);
                store.put("flyway.flywayTest", flywayTest);
            });
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        Optional<FlywayTestConfiguration> configuration = getConfiguration(context);
        if (configuration.isEmpty()) {
            return;
        }

        Flyway flyway = flyway(configuration.get(), context);
        if (configuration.get().isClean()) {
            flyway.clean();
        }

        flyway.migrate();
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        getConfiguration(context)
            .filter(FlywayTestConfiguration::isClean)
            .map(configuration -> flyway(configuration, context))
            .ifPresent(Flyway::migrate);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        getConfiguration(context)
            .filter(FlywayTestConfiguration::isClean)
            .map(configuration -> flyway(configuration, context))
            .ifPresent(Flyway::clean);
    }

    private Flyway flyway(final FlywayTestConfiguration configuration, final ExtensionContext context) {
        FluentConfiguration flywayConfiguration = Flyway.configure().loadDefaultConfigurationFiles().envVars();

        // Add locations
        List<Location> locations = new ArrayList<>(Arrays.asList(flywayConfiguration.getLocations()));
        locations.add(new Location("db/" + context.getRequiredTestClass().getName().replaceAll("\\.", "/")));
        configuration.getLocations().stream().map(Location::new).forEach(locations::add);
        flywayConfiguration.locations(locations.toArray(new Location[]{}));

        // Datasource
        if (configuration.getDatasourceInfo().getUrl() != null) {
            flywayConfiguration.dataSource(
                configuration.getDatasourceInfo().getUrl(),
                configuration.getDatasourceInfo().getUsername(),
                configuration.getDatasourceInfo().getPassword());
        }

        if (flywayConfiguration.getConnectRetries() == 0) {
            flywayConfiguration.connectRetries(120);
        }

        return flywayConfiguration.load();
    }

    private Optional<FlywayTestConfiguration> getConfiguration(final ExtensionContext context) {
        List<FlywayTest> flywayAnnotations = findFlywayAnnotations(context);

        if (flywayAnnotations.isEmpty()) {
            return Optional.empty();
        }

        final String url =
            flywayAnnotations
                .stream()
                .map(FlywayTest::value)
                .map(DataSource::url)
                .filter(FlywayExtension::isNotEmpty)
                .findFirst()
                .orElse(null);

        final String username =
            flywayAnnotations
                .stream()
                .map(FlywayTest::value)
                .map(DataSource::username)
                .filter(FlywayExtension::isNotEmpty)
                .findFirst()
                .orElse(null);

        final String password =
            flywayAnnotations
                .stream()
                .map(FlywayTest::value)
                .map(DataSource::password)
                .filter(FlywayExtension::isNotEmpty)
                .findFirst()
                .orElse(null);

        final List<String> locations =
            flywayAnnotations
                .stream()
                .map(FlywayTest::additionalLocations)
                .flatMap(Stream::of).collect(toList());

        final Boolean clean =
            flywayAnnotations
                .stream()
                .map(FlywayTest::clean)
                .findFirst()
                .orElse(true);

        final DataSourceInfo datasourceInfo =
            flywayAnnotations
                .stream()
                .map(FlywayTest::value)
                .map(DataSource::value)
                .map(FlywayExtension::newDatasourceProvider)
                .filter(provider -> !(provider instanceof DataSource.DEFAULT))
                .map(provider -> provider.getDatasourceInfo(context))
                .findFirst()
                .orElse(DataSourceInfo.config(url, username, password));

        return Optional.of(FlywayTestConfiguration.flywayTestConfiguration(datasourceInfo, locations, clean));
    }

    private List<FlywayTest> findFlywayAnnotations(final ExtensionContext context) {
        List<FlywayTest> flywayTestAnnotations = new ArrayList<>();

        // Method
        context.getTestMethod().flatMap(klass -> findAnnotation(klass, FlywayTest.class)).ifPresent(flywayTestAnnotations::add);

        // Main Class or Nested Class
        findFlywayAnnotation(context).ifPresent(flywayTestAnnotations::add);

        // Enclosing Class
        Optional<ExtensionContext> parentContext = context.getParent();
        while (parentContext.isPresent()) {
            findFlywayAnnotation(parentContext.get()).ifPresent(flywayTestAnnotations::add);
            parentContext = parentContext.get().getParent();
        }

        return flywayTestAnnotations;
    }

    private Optional<FlywayTest> findFlywayAnnotation(final ExtensionContext context) {
        final FlywayTest flywayTest = context.getStore(FLYWAY_EXTENSION).get("flyway.flywayTest", FlywayTest.class);
        if (flywayTest != null) {
            return Optional.of(flywayTest);
        }

        return context.getTestClass().flatMap(klass -> findAnnotation(klass, FlywayTest.class, true));
    }

    private static DataSourceProvider newDatasourceProvider(final Class<? extends DataSourceProvider> klass) {
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static boolean isNotEmpty(final String s) {
        return !s.isEmpty();
    }

    private static class FlywayTestConfiguration {
        private DataSourceInfo datasourceInfo;
        private List<String> locations;
        private boolean clean;

        private FlywayTestConfiguration(
            final DataSourceInfo datasourceInfo,
            final List<String> locations,
            final boolean clean) {
            this.datasourceInfo = datasourceInfo;
            this.locations = locations;
            this.clean = clean;
        }

        private static FlywayTestConfiguration flywayTestConfiguration(
            final DataSourceInfo datasourceInfo,
            final List<String> locations,
            final boolean clean) {
            return new FlywayTestConfiguration(datasourceInfo, locations, clean);
        }

        DataSourceInfo getDatasourceInfo() {
            return datasourceInfo;
        }

        List<String> getLocations() {
            return locations;
        }

        boolean isClean() {
            return clean;
        }
    }
}
