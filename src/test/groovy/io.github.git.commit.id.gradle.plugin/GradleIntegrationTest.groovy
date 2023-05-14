package io.github.git.commit.id.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GradleIntegrationTest extends AbstractGradleTest {
    private GradleRunner createRunner(
            File projectDir,
            List<String> extraArgs=Collections.emptyList()) {
        GradleRunner.create()
                .withPluginClasspath()
                .withArguments(
                        ":${GitCommitIdPluginGenerationTask.NAME}", "--stacktrace", "--debug",
                        *extraArgs
                )
                .withProjectDir(projectDir)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    void pluginCanBeSkippedByConfiguration(boolean shouldSkip) {
        given: "a dummy project"
        def projectDir = temporaryFolder

        new File(projectDir, "build.gradle").withWriterAppend("UTF-8") {
            it.write(
                    """
                    ${GitCommitIdPluginExtension.NAME} {
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

        then: "the execution should run the plugin"
        def result = runner.build()
        assertPluginExecuted(result)

        and: "running it again should not run the plugin again"
        result = runner.build()
        assertPluginSkipped(result)

        when: "we add a commit to git"
        new File(projectDir, "README.md") << """
            Hello World!
        """.stripIndent()
        runGitAdd(projectDir, "README.md")
        runGitCommit(projectDir, "added readme")

        and: "the plugin get's executed again"
        result = runner.build()
        assertPluginExecuted(result)
    }

    @Test
    void cachingShouldWork() {
        given: "a dummy project"
        def projectDir = temporaryFolder

        and: "caching is enabled"
        // https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_enable
        new File(projectDir, "gradle.properties") << "org.gradle.caching=true"

        when: "running the plugin"
        def runner = createRunner(projectDir)

        then: "the execution should run the plugin"
        def result = runner.build()
        assertPluginExecuted(result)

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

    @Test
    void propertiesAreExposedToProject() {
        given: "a dummy project"
        def projectDir = temporaryFolder
        def marker = "==============MARKER=============="

        // and: "caching is enabled"
        // https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_enable
        // new File(projectDir, "gradle.properties") << "org.gradle.caching=true"

        and: "we have a dummy task that consumes the generated properties"
        new File(projectDir, "build.gradle").withWriterAppend("UTF-8") {
            it.write(
                    """
                    task printPropTask(type: DefaultTask) {
                        outputs.upToDateWhen { false }
                        // dependsOn (tasks.gitCommitIdGenerationTask)
                        doLast {
                            // println("${marker}1: \${project?.ext?.gitProperties}${marker}")
                            // println("${marker}2: \${project?.ext?.gitProperties.get('git.commit.id.full')}${marker}")
                            // println("${marker}3: \${project?.ext?.gitProperties.get('git.commit.id.abbrev', 'EMPTY')}${marker}")
                            // println("${marker}4: \${project?.ext?.gitProperties()}${marker}")
                            // println("${marker}5: \${project?.ext?.gitProperties('git.commit.id.full')}${marker}")
                            // println("${marker}6: \${project?.ext?.gitProperties('git.commit.id.abbrev')}${marker}")
                            // println("${marker}7: \${project?.ext?.gitProperties['git.commit.id.full']}${marker}")
                            println("${marker}\${project?.ext?.gitProperties['git.commit.id.abbrev']}${marker}")
                        }
                    }
                    """.stripIndent()
            )
        }

        when: "running the plugin"
        def runner = createRunner(projectDir, [":printPropTask"])

        then: "the execution should run the plugin"
        def result = runner.build()
        assertPluginExecuted(result)

        and: "the output contains the abbrivated commit from the repository"
        def expectedAbbrevCommit = getAbbrevCommit(projectDir)
        def markerLine = result.output.readLines().find {it.contains(marker)}
        Assertions.assertTrue(markerLine.contains("${marker}${expectedAbbrevCommit}${marker}"), markerLine)

        and: "running it again should not run the plugin again"
        result = runner.build()
        assertPluginSkipped(result)

        and: "the output contains the abbrivated commit from the repository"
        markerLine = result.output.readLines().find {it.contains(marker)}
        Assertions.assertTrue(markerLine.contains("${marker}${expectedAbbrevCommit}${marker}"), markerLine)
    }
}
