package io.github.git.commit.id.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GitCommitIdPlugin implements Plugin<Project> {
    public void apply(Project project) {
        var extension = project.getExtensions().create(
                "git_commit_id", GitCommitIdPluginExtension.class, project);
        var task = project.getTasks().create("gitCommitIdGenerationTask", GitCommitIdPluginGenerationTask.class);
        task.onlyIf(ignore -> extension.skip.get() == false);
    }
}