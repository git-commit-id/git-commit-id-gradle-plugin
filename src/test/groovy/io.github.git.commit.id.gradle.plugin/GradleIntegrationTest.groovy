package io.github.git.commit.id.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GradleIntegrationTest extends AbstractGradleTest {
    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    void pluginCanBeSkippedByConfiguration(boolean shouldSkip) {
        given: "a dummy project"
        def projectDir = temporaryFolder

        new File(projectDir, "build.gradle").withWriterAppend("UTF-8") {
            it.write(
                    """
                    ${GitCommitIdPlugin.GIT_COMMIT_ID_EXTENSION_NAME} {
                        skip.set($shouldSkip)
                    }
                    """.stripIndent()
            )
        }

        when: "running the plugin"
        def runner = GradleRunner.create()
                .withPluginClasspath()
                .withArguments(":${GitCommitIdPlugin.GIT_COMMIT_ID_TASK_NAME}", "--stacktrace", "--debug")
                .withProjectDir(projectDir)

        then: "the execution should be successfull"
        def result = runner.build()
        Assertions.assertEquals(
                shouldSkip ? TaskOutcome.SKIPPED : TaskOutcome.SUCCESS,
                result.task(":${GitCommitIdPlugin.GIT_COMMIT_ID_TASK_NAME}")?.outcome,
                result.output
        )
    }
}
