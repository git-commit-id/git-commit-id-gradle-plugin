package io.github.git.commit.id.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GitCommitIdPlugin implements Plugin<Project> {
    public static final String GIT_COMMIT_ID_EXTENSION_NAME = "gitCommitId";
    public static final String GIT_COMMIT_ID_TASK_NAME = "gitCommitIdGenerationTask";

    public void apply(Project project) {
        var extension = project.getExtensions().create(
                GIT_COMMIT_ID_EXTENSION_NAME, GitCommitIdPluginExtension.class, project);
        var task = project.getTasks().create(GIT_COMMIT_ID_TASK_NAME, GitCommitIdPluginGenerationTask.class);
        task.onlyIf(ignore -> extension.skip.get() == false);
    }
}