package com.radcortez.flyway.test.junit;

import com.radcortez.flyway.test.annotation.FlywayTest;
import com.radcortez.flyway.test.annotation.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static java.io.File.separator;
import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class FlywayExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeAll(final ExtensionContext context) {
        final Optional<FlywayTestConfiguration> configuration = findFlywayAnnnotation(context);
        if (configuration.isEmpty()) {
            return;
        }

        final Flyway flyway = flyway(configuration.get(), context);
        if (configuration.get().isClean()) {
            flyway.clean();
        }

        flyway.migrate();
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        findFlywayAnnnotation(context)
            .filter(FlywayTestConfiguration::isClean)
            .map(configuration -> flyway(configuration, context))
            .ifPresent(Flyway::migrate);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        findFlywayAnnnotation(context)
            .filter(FlywayTestConfiguration::isClean)
            .map(configuration -> flyway(configuration, context))
            .ifPresent(Flyway::clean);
    }

    private Flyway flyway(final FlywayTestConfiguration configuration, final ExtensionContext context) {
        final String packageName = context.getRequiredTestClass().getName();
        final String testDefaultLocation = "db/" + packageName.replaceAll("\\.", Matcher.quoteReplacement(separator));
        final List<String> locations = new ArrayList<>(configuration.getLocations());
        locations.add(testDefaultLocation);
        locations.add("db" + separator + "migration");

        return Flyway.configure()
                     .dataSource(configuration.getDatasourceInfo().getUrl(),
                                 configuration.getDatasourceInfo().getUsername(),
                                 configuration.getDatasourceInfo().getPassword())
                     .connectRetries(120)
                     .locations(locations.toArray(String[]::new))
                     .load();
    }

    private Optional<FlywayTestConfiguration> findFlywayAnnnotation(final ExtensionContext context) {
        final Optional<FlywayTest> classAnnotation =
            context.getTestClass().flatMap(klass -> findAnnotation(klass, FlywayTest.class));
        final Optional<FlywayTest> methodAnnotation =
            context.getTestMethod().flatMap(klass -> findAnnotation(klass, FlywayTest.class));

        if (Stream.of(classAnnotation, methodAnnotation).flatMap(Optional::stream).findAny().isEmpty()) {
            return Optional.empty();
        }

        final String url =
            Stream.of(methodAnnotation, classAnnotation)
                  .flatMap(Optional::stream)
                  .map(FlywayTest::value)
                  .map(DataSource::url)
                  .filter(FlywayExtension::isNotEmpty)
                  .findFirst()
                  .orElse("");

        final String username =
            Stream.of(methodAnnotation, classAnnotation)
                  .flatMap(Optional::stream)
                  .map(FlywayTest::value)
                  .map(DataSource::username)
                  .filter(FlywayExtension::isNotEmpty)
                  .findFirst()
                  .orElse("");

        final String password =
            Stream.of(methodAnnotation, classAnnotation)
                  .flatMap(Optional::stream)
                  .map(FlywayTest::value)
                  .map(DataSource::password)
                  .filter(FlywayExtension::isNotEmpty)
                  .findFirst()
                  .orElse("");

        final List<String> locations =
            Stream.of(methodAnnotation, classAnnotation)
                  .flatMap(Optional::stream)
                  .map(FlywayTest::additionalLocations)
                  .flatMap(Stream::of).collect(toList());

        final Boolean clean =
            Stream.of(methodAnnotation, classAnnotation)
                  .flatMap(Optional::stream)
                  .map(FlywayTest::clean)
                  .findFirst()
                  .orElse(true);

        final DataSourceInfo datasourceInfo =
            Stream.of(methodAnnotation, classAnnotation)
                  .flatMap(Optional::stream)
                  .map(FlywayTest::value)
                  .map(DataSource::value)
                  .map(FlywayExtension::newDatasourceProvider)
                  .filter(provider -> !(provider instanceof DataSource.DEFAULT))
                  .map(provider -> provider.getDatasourceInfo(context))
                  .findFirst()
                  .orElse(DataSourceInfo.config(url, username, password));

        if (datasourceInfo.getUrl() == null) {
            throw new IllegalStateException("No jdbc url provided.");
        }

        return Optional.of(FlywayTestConfiguration.flywayTestConfiguration(datasourceInfo, locations, clean));
    }

    private static DataSourceProvider newDatasourceProvider(final Class<? extends DataSourceProvider> klass) {
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException();
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
