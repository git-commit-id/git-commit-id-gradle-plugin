package io.github.git.commit.id.gradle.plugin

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

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
                id('io.github.git-commit-id.gradle-plugin')
            }
        """.stripIndent()

        // and: "it's initialized as git project"
        runGit(projectDir, ["init"], [])
        runGit(projectDir, ["add", "*"], [])
        runGit(projectDir, ["commit", "-m", "initialize dummy project"])
    }

    protected boolean runGit(projectDir, gitCommands, extraGitArgs=["--no-signoff", "--no-gpg-sign"]) {
        def command = [
            "git", "-C", projectDir, *gitCommands
        ]
        if (extraGitArgs) {
            command.addAll(*extraGitArgs)
        }

        def p = command.execute()
        def exitCode = p.waitFor()
        if (exitCode != 0) {
            def error = new String(p.errorStream.readAllBytes())
            throw new RuntimeException("Running '$command' failed with '$exitCode'. Error:\n$error")
        }
    }
}
