package io.github.git.commit.id.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GitCommitIdPlugin implements Plugin<Project> {
    public void apply(Project project) {
        var extension = project.getExtensions().create(
                "git-commit-id", GitCommitIdPluginExtension.class, project);
        var task = project.getTasks().register("gitCommitIdGenerationTask", GitCommitIdPluginGenerationTask.class);
    }
}