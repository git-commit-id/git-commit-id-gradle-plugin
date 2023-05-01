package io.github.git.commit.id.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
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

        when: "running the plugin"
        def runner = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withPluginClasspath()
                .withArguments(*extraExecutionArgs, "--stacktrace", "--debug")
                .withProjectDir(projectDir)

        then: "the execution should be successfull"
        def result = runner.build()
        Assertions.assertEquals(
                TaskOutcome.SUCCESS,
                result.task(":${GitCommitIdPlugin.GIT_COMMIT_ID_TASK_NAME}")?.outcome,
                result.output
        )

        and: "running it again should yield an up-to-date"
        result = runner.build()
        Assertions.assertEquals(
                TaskOutcome.UP_TO_DATE,
                result.task(":${GitCommitIdPlugin.GIT_COMMIT_ID_TASK_NAME}")?.outcome,
                result.output
        )
    }

    private static Stream<Arguments> getGradleTestParams() {
        return Stream.of(
                Arguments.of("8.1.1", Arrays.asList("${GitCommitIdPlugin.GIT_COMMIT_ID_TASK_NAME}")),
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
