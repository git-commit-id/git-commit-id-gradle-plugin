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

import groovy.lang.Closure;
import java.util.Map;
import java.util.Properties;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import pl.project13.core.CommitIdPropertiesOutputFormat;
import pl.project13.core.GitCommitIdExecutionException;
import pl.project13.core.util.GenericFileManager;

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
    private static final class PropertyExposingClosure extends Closure<String> {
        private final GitCommitIdPluginGenerationTask task;

        public PropertyExposingClosure(Object owner, GitCommitIdPluginGenerationTask task) {
            super(owner, owner);
            this.task = task;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            for (Map.Entry<Object, Object> e : getProps().entrySet()) {
                s.append(e.getValue());
                s.append(":");
                s.append(e.getKey());
                s.append(",");
            }
            return s.toString();
        }

        private Properties getProps() {
            try {
                final Properties p = GenericFileManager.readPropertiesAsUtf8(
                    // task.getGitCommitIdPluginOutputSettingsExtension().getOutputFormat().get(),
                    CommitIdPropertiesOutputFormat.PROPERTIES,
                    task.getInternalOutput().getAsFile()
                );
                return p;
            } catch (GitCommitIdExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object getProperty(String property) {
            return getProps().get(property);
        }

        public String doCall() {
            return toString();
        }

        public String doCall(Object obj) {
            return toString();
        }

        public String doCall(String property) {
            return (String) getProps().get(property);
        }

        // Do NOT IMPLEMENT!
        // https://issues.apache.org/jira/browse/GROOVY-1665
        // public String doCall(Object obj1, Object obj2) {
        // }
        public String get(String property) {
            return (String) getProps().get(property);
        }

        public String get(String property, String defaultString) {
            return (String) getProps().getOrDefault(property, defaultString);
        }
    }

    /**
     * Apply this plugin to the given target project.
     *
     * @param project The target project
     */
    public void apply(Project project) {
        // gitCommitId
        var extension = project.getExtensions().create(
            GitCommitIdPluginExtension.NAME,
            GitCommitIdPluginExtension.class);
        // gitCommitId -> outputSettings
        ((ExtensionAware) extension).getExtensions().create(
            GitCommitIdPluginOutputSettingsExtension.NAME,
            GitCommitIdPluginOutputSettingsExtension.class);
        // gitCommitId -> gitSettings
        ((ExtensionAware) extension).getExtensions().create(
            GitCommitIdPluginGitSettingsExtension.NAME,
            GitCommitIdPluginGitSettingsExtension.class);
        // gitCommitId -> formatSettings
        ((ExtensionAware) extension).getExtensions().create(
            GitCommitIdPluginFormatSettingsExtension.NAME,
            GitCommitIdPluginFormatSettingsExtension.class);
        // gitCommitId -> filterSettings
        ((ExtensionAware) extension).getExtensions().create(
            GitCommitIdPluginFilterSettingsExtension.NAME,
            GitCommitIdPluginFilterSettingsExtension.class);
        // Task
        var task = project.getTasks().create(
            GitCommitIdPluginGenerationTask.NAME,
            GitCommitIdPluginGenerationTask.class);
        task.onlyIf(ignore -> extension.getSkip().get() == false);

        project
          .getExtensions()
          .getExtraProperties()
            .set("gitProperties", new PropertyExposingClosure(this, task));
    }
}