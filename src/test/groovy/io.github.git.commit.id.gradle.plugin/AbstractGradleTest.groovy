package io.github.git.commit.id.gradle.plugin

import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir

class AbstractGradleTest {
    // @TempDir(cleanup = CleanupMode.ON_SUCCESS)  // For debugging
    @TempDir(cleanup = CleanupMode.ALWAYS)
    File temporaryFolder

    @BeforeEach
    void initDummyProject() {
        def projectDir = temporaryFolder
        new File(projectDir, "settings.gradle") << ""
        new File(projectDir, "build.gradle") << """
            plugins {
                id('java')
                id('io.github.git-commit-id.git-commit-id-gradle-plugin')
            }
        """.stripIndent()

        // and: "it's initialized as git project"
        runGitInitAddAndCommit(projectDir)
    }

    protected void runGitInitAddAndCommit(File projectDir) {
        try (final Git git = Git.init().setDirectory(projectDir).call()) {
            // nothing
        }
        runGitAdd(projectDir)
        runGitCommit(projectDir)
    }

    protected void runGitAdd(File projectDir, String filePattern = "*") {
        try (final Git git = Git.open(projectDir)) {
            git.add().addFilepattern(filePattern).call()
        }
    }

    protected String runGitCommit(File projectDir, String message = "dummy commit") {
        try (final Git git = Git.open(projectDir)) {
            return git.commit()
                    .setAuthor("JUnitTest", "example@example.com")
                    .setMessage(message)
                    .call()
                    .name()
        }
    }

    protected String getAbbrevCommit(File projectDir) {
        try (final Git git = Git.open(projectDir)) {
            var log = git.log().setMaxCount(1).call()[0]
            return log.abbreviate(7).name()
        }
    }

    protected void assertTaskOutcome(
            BuildResult result,
            TaskOutcome expectedTaskOutcome,
            String taskName=":${GitCommitIdPluginGenerationTask.NAME}") {
        Assertions.assertEquals(
                expectedTaskOutcome,
                result.task(taskName)?.outcome,
                result.output
        )
    }

    protected void assertPluginExecuted(BuildResult result) {
        assertTaskOutcome(result, TaskOutcome.SUCCESS)
        Assertions.assertTrue(
                result.output.contains(GitCommitIdPluginGenerationTask.PLUGIN_EXECUTION_MESSAGE),
                result.output
        )
    }

    protected void assertPluginSkipped(BuildResult result) {
        assertTaskOutcome(result, TaskOutcome.UP_TO_DATE)
        Assertions.assertFalse(
                result.output.contains(GitCommitIdPluginGenerationTask.PLUGIN_EXECUTION_MESSAGE),
                result.output
        )
    }

}
