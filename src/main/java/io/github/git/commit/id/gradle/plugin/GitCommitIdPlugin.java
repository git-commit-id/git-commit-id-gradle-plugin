/*
 * This file is part of git-commit-id-gradle-plugin.
 *
 * git-commit-id-gradle-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * git-commit-id-gradle-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with git-commit-id-gradle-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.git.commit.id.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import pl.project13.core.CommitIdPropertiesOutputFormat;

/**
 * The GitCommitIdPlugin or also known as git-commit-id-gradle-plugin is a plugin
 * allows you to generate various information about your git repository
 * and include the generated information in your final product (e.g. java artifacts).
 * For most users the most relevant is likely the git version
 * from which the project was built from. If such information is included
 * a project can move closer to the goal of achieving a
 * <a href="https://reproducible-builds.org/docs/definition/">reproducible build</a>.
 *
 *
 * <h1>Usage</h1>
 * To make use of the plugin you must apply the plugin in your `build.gradle` as mentioned in the
 * <a href="https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block">gradle documentation</a>:
 *
 * <pre>
 * plugins {
 *     id 'java'
 *     id 'io.github.git-commit-id.git-commit-id-gradle-plugin'
 * }
 * </pre>
 * or if you want to use a specific version of the plugin via:
 * <pre>
 * plugins {
 *     id 'java'
 *     id 'io.github.git-commit-id.git-commit-id-gradle-plugin' version 'MAJOR.MINOR.PATCH'
 * }
 * </pre>
 *
 * <p>This plugin follows the <a href="https://semver.org/">Semantic Versioning</a> that roughly can be
 * summarized as a version number {@code MAJOR.MINOR.PATCH}, increment the:
 * <ul>
 *     <li>{@code MAJOR} version when you make incompatible API changes</li>
 *     <li>{@code MINOR} version when you add functionality in a backwards compatible manner</li>
 *     <li>{@code PATCH} version when you make backwards compatible bug fixes</li>
 * </ul>
 *
 * <h1>Configuration</h1>
 * Refer to the {@link GitCommitIdPluginExtension} class in the API documentation.
 * If you are interested in the task that generates the actual information refer
 * to {@link GitCommitIdPluginGenerationTask}.
 */
public class GitCommitIdPlugin implements Plugin<Project> {
    public static final String GIT_COMMIT_ID_EXTENSION_NAME = "gitCommitId";
    public static final String GIT_COMMIT_ID_TASK_NAME = "gitCommitIdGenerationTask";

    /**
     * Apply this plugin to the given target project.
     *
     * @param project The target project
     */
    public void apply(Project project) {
        var extension = project.getExtensions().create(
                GIT_COMMIT_ID_EXTENSION_NAME, GitCommitIdPluginExtension.class);
        ((ExtensionAware) extension).getExtensions().create(
            "outputSettings", GitCommitIdPluginOutputSettingsExtension.class);
        var task = project.getTasks().create(
                GIT_COMMIT_ID_TASK_NAME, GitCommitIdPluginGenerationTask.class);
        task.onlyIf(ignore -> extension.getSkip().get() == false);
    }
}