package io.github.git.commit.id.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.Stream

class GradleCompabilityTest extends AbstractGradleTest {
    @ParameterizedTest
    @MethodSource("getGradleTestParams")
    void testPluginSupported(String gradleVersion, List<String> extraExecutionArgs) {
        given: "a dummy project"
        def projectDir = temporaryFolder

        // and: "caching is enabled"
        // https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_enable
        // new File(projectDir, "gradle.properties") << "org.gradle.caching=true"

        when: "running the plugin"
        def runner = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withPluginClasspath()
                .withArguments(*extraExecutionArgs, "--stacktrace", "--debug")
                .withProjectDir(projectDir)

        then: "the execution should run the plugin"
        def result = runner.build()
        assertPluginExecuted(result)

        and: "running it again should not run the plugin again"
        result = runner.build()
        assertPluginSkipped(result)
    }

    private static Stream<Arguments> getGradleTestParams() {
        return Stream.of(
                Arguments.of("8.1.1", Arrays.asList("${GitCommitIdPluginGenerationTask.NAME}")),
                /*
                Arguments.of("8.1"),
                Arguments.of("8.0.2"),
                Arguments.of("8.0.1"),
                Arguments.of("8.0"),
                Arguments.of("7.6.1"),
                Arguments.of("7.6"),
                Arguments.of("7.5.1"),
                Arguments.of("7.5"),
                Arguments.of("7.4.2"),
                Arguments.of("7.4.1"),
                Arguments.of("7.4"),
                Arguments.of("7.3.3"),
                Arguments.of("7.3.2"),
                Arguments.of("7.3.1"),
                Arguments.of("7.3"),
                Arguments.of("7.2"),
                Arguments.of("7.1.1"),
                Arguments.of("7.1"),
                Arguments.of("7.0.2"),
                Arguments.of("7.0.1"),
                Arguments.of("7.0"),
                Arguments.of("6.9.4"),
                Arguments.of("6.9.3"),
                Arguments.of("6.9.2"),
                Arguments.of("6.9.1"),
                Arguments.of("6.9"),
                Arguments.of("6.8.3"),
                Arguments.of("6.8.2"),
                Arguments.of("6.8.1"),
                Arguments.of("6.8"),
                Arguments.of("6.7.1"),
                Arguments.of("6.7"),
                Arguments.of("6.6.1"),
                Arguments.of("6.6"),
                Arguments.of("6.5.1"),
                Arguments.of("6.5"),
                Arguments.of("6.4.1"),
                Arguments.of("6.4"),
                Arguments.of("6.3"),
                Arguments.of("6.2.2"),
                Arguments.of("6.2.1"),
                Arguments.of("6.2"),
                Arguments.of("6.1.1"),
                Arguments.of("6.1"),
                Arguments.of("6.0.1"),
                Arguments.of("6.0"),
                Arguments.of("5.6.4"),
                Arguments.of("5.6.3"),
                Arguments.of("5.6.2"),
                Arguments.of("5.6.1"),
                Arguments.of("5.6"),
                Arguments.of("5.5.1"),
                Arguments.of("5.5"),
                Arguments.of("5.4.1"),
                Arguments.of("5.4"),
                Arguments.of("5.3.1"),
                Arguments.of("5.3"),
                Arguments.of("5.2.1"),
                Arguments.of("5.2"),
                Arguments.of("5.1"),
                // Doesn't support conventions: https://docs.gradle.org/5.1/release-notes.html#specify-a-convention-for-a-property
                Arguments.of("5.0"),
                 */
        )
    }
}
