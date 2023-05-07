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

import javax.inject.Inject;
import org.gradle.api.provider.Property;

/**
 * The {@link GitCommitIdPlugin} comes with a sensible set of default configurations and settings.
 * However, there might be cases where a default doesn't fit your project needs.
 * Gradle allows user to configure with
 * <a href="https://docs.gradle.org/current/userguide/implementing_gradle_plugins.html#modeling_dsl_like_apis">Modeling DSL-like APIs</a>.
 * Perhaps one additional item you need to know about configuration is that the
 * {@link GitCommitIdPlugin} makes use of gradle's
 * <a href="https://docs.gradle.org/current/userguide/lazy_configuration.html">Lazy Configuration</a>
 * which is gradle's way to manage growing build complexity.
 *
 * <p>This Extension exposes all configuration options for the
 * {@link GitCommitIdPlugin} as extension.
 * If you are interested in the task that generates the actual information refer to
 * {@link GitCommitIdPluginGenerationTask}.
 *
 * <p>In short the plugin can be configured by adding a block like this
 * to your {@code build.gradle}:
 * <pre>
 * gitCommitId {
 *     skip.set(false)
 * }
 * </pre>
 */
public abstract class GitCommitIdPluginExtension {
    /**
     * Name of the extension how it's made available to the end-user as
     * DSL like configuration in the {@code build.gradle}:
     * <pre>
     * gitCommitId {
     *     skip.set(false)
     * }
     * </pre>.
     */
    public static final String NAME = "gitCommitId";

    /**
     * Configuration option to enable or disable more verbose information during the
     * execution of the plugin.
     * To enable such more debugging messages, set this to {@code true}.
     * By default, this setting is disabled (set to {@code false}).
     */
    public abstract Property<Boolean> getVerbose();

    /**
     * Allows to configure to skip the execution of the {@link GitCommitIdPluginGenerationTask}
     * that will generate the information you have requested from this plugin.
     *
     * <p>By default this is set to {@code false}.
     */
    public abstract Property<Boolean> getSkip();

    /**
     * When set to {@code true} this plugin will try to use the branch name from build environment.
     * Set to {@code false} to use JGit/GIT to get current branch name which can be useful
     * when using the JGitflow maven plugin. I'm not sure if there are similar plugins for gradle
     * where this needs to set to {@code false}.
     *
     * <p>By default this is set to {@code true}.
     */
    public abstract Property<Boolean> getUseBranchNameFromBuildEnvironment();

    /**
     * Setup the default values / conventions for the GitCommitIdPluginExtension.
     */
    @Inject
    public GitCommitIdPluginExtension() {
        // injectAllReactorProjects
        getVerbose().convention(false);
        // skipPoms
        getSkip().convention(false);
        // commitIdGenerationMode
        // replacementProperties
        getUseBranchNameFromBuildEnvironment().convention(true);
        // injectIntoSysProperties
        // projectBuildOutputTimestamp
    }
}
