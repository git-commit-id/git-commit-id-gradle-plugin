package io.github.git.commit.id.gradle.plugin

import org.eclipse.jgit.api.Git
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

    protected void runGitCommit(File projectDir, String message = "dummy commit") {
        try (final Git git = Git.open(projectDir)) {
            git.commit()
                    .setAuthor("JUnitTest", "example@example.com")
                    .setMessage(message)
                    .call()
        }
    }
}
