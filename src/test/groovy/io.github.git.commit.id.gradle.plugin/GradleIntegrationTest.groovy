package io.github.git.commit.id.gradle.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GradleIntegrationTest extends AbstractGradleTest {
    private GradleRunner createRunner(projectDir) {
        GradleRunner.create()
                .withPluginClasspath()
                .withArguments(":${GitCommitIdPlugin.GIT_COMMIT_ID_TASK_NAME}", "--stacktrace", "--debug")
                .withProjectDir(projectDir)
    }

    private void assertTaskOutcome(BuildResult result, TaskOutcome expectedTaskOutcome) {
        Assertions.assertEquals(
                expectedTaskOutcome,
                result.task(":${GitCommitIdPlugin.GIT_COMMIT_ID_TASK_NAME}")?.outcome,
                result.output
        )
    }

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
        def runner = createRunner(projectDir)

        then: "the execution should be successfull"
        def result = runner.build()
        assertTaskOutcome(result, shouldSkip ? TaskOutcome.SKIPPED : TaskOutcome.SUCCESS)
    }

    @Test
    void upToDateChecksShouldWork() {
        given: "a dummy project"
        def projectDir = temporaryFolder

        when: "running the plugin"
        def runner = createRunner(projectDir)

        then: "the execution should be successfull"
        def result = runner.build()
        assertTaskOutcome(result, TaskOutcome.SUCCESS)

        and: "running it again should yield an up-to-date"
        result = runner.build()
        assertTaskOutcome(result, TaskOutcome.UP_TO_DATE)

        when: "we add a commit to git"
        new File(projectDir, "README.md") << """
            Hello World!
        """.stripIndent()
        runGit(projectDir, ["add", "README.md"], [])
        runGit(projectDir, ["commit", "-m", "added readme"])

        and: "the plugin get's executed again"
        result = runner.build()
        assertTaskOutcome(result, TaskOutcome.SUCCESS)
    }

    @Test
    void cachingShouldWork() {
        given: "a dummy project"
        def projectDir = temporaryFolder

        and: "caching is enabled"
        // https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_enable
        new File(projectDir, "gradle.properties") << "org.gradle.caching=true"

        and: "we produce a git properties file"
        new File(projectDir, "build.gradle").withWriterAppend("UTF-8") {
            it.write(
                    """
                    ${GitCommitIdPlugin.GIT_COMMIT_ID_EXTENSION_NAME} {
                        generateGitPropertiesFile.set(true)
                    }
                    """.stripIndent()
            )
        }

        when: "running the plugin"
        def runner = createRunner(projectDir)

        then: "the execution should be successfull"
        def result = runner.build()
        assertTaskOutcome(result, TaskOutcome.SUCCESS)

        and: "output exists"
        def expectedGenerated = projectDir.toPath().resolve("build/git.properties").toFile()
        Assertions.assertTrue(expectedGenerated.exists(), "Does not exists $expectedGenerated")
        def originalLines = expectedGenerated.readLines()

        and: "we delete the file by accident"
        expectedGenerated.delete()
        Assertions.assertFalse(expectedGenerated.exists(), "Should not exists $expectedGenerated")

        when: "the plugin get's executed again"
        result = runner.build()
        assertTaskOutcome(result, TaskOutcome.FROM_CACHE)
        Assertions.assertTrue(expectedGenerated.exists(), "Was not regenerated $expectedGenerated")

        and:
        def newlyGeneratedLines = expectedGenerated.readLines()
        Assertions.assertEquals(originalLines, newlyGeneratedLines)
    }
}
