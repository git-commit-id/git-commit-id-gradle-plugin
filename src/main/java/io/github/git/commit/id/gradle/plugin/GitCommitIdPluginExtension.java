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

import java.util.Collections;
import java.util.TimeZone;
import javax.inject.Inject;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import pl.project13.core.git.GitDescribeConfig;

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
     * This configuration allows you to configure the "prefix" of the generated properties and thus
     * will be used as the "namespace" prefix for all exposed/generated properties.
     *
     * <p>An example the plugin may generate the property {@code ${configured-prefix}.commit.id}.
     * Such behaviour can be used to generate properties for multiple git repositories.
     *
     * <p>Refer to {@link pl.project13.core.GitCommitPropertyConstant} for all properties
     * that can be generated. The configured prefix would need to be added in front.
     *
     * <p>Defaults to {@code git} which then results in {@code git.commit.id} and
     * similar generated properties.
     */
    public abstract Property<String> getPropertyPrefix();

    /**
     * Allows to configure the date format in which "time" properties should be converted into.
     * For example the commit time, or the author time would be converted into the specified format.
     *
     * <p>Defaults to {@code yyyy-MM-dd'T'HH:mm:ssZ}
     *
     * <p>Refer to {@link #getExportDateFormatTimeZone()} if you want to change the time-zone.
     */
    public abstract Property<String> getExportDateFormat();

    /**
     * Allows to configure the time zone which is utilized for {@link #getExportDateFormat()}.
     *
     * <p>Defaults to {@code java.util.TimeZone.getDefault().getID()}.
     * Allows various formats of timezone configuration
     * (e.g. 'America/Los_Angeles', 'GMT+10', 'PST').
     * As a general warning try to avoid three-letter time zone IDs because the same
     * abbreviation are often used for multiple time zones.
     */
    public abstract Property<String> getExportDateFormatTimeZone();

    /**
     * Allows to configure to skip the execution of the {@link GitCommitIdPluginGenerationTask}
     * that will generate the information you have requested from this plugin.
     *
     * <p>By default this is set to {@code false}.
     */
    public abstract Property<Boolean> getSkip();

    /**
     * Can be used to exclude certain properties from being emitted (e.g. filter out properties
     * that you *don't* want to expose). May be useful when you want to hide
     * {@code git.build.user.email} (maybe because you don't want to expose your eMail?),
     * or the email of the committer?
     *
     * <p>Each value may be globbing, that is, you can write {@code git.commit.user.*} to
     * exclude both the {@code name}, as well as {@code email} properties from being emitted.
     *
     * <p>Please note that the strings here are Java regexes ({@code .*} is globbing,
     * not plain {@code *}).
     * If you have a very long list of exclusions you may want to
     * use {@link #getIncludeOnlyProperties()}.
     *
     * <p>Defaults to the empty list (= no properties are excluded).
     */
    public abstract ListProperty<String> getExcludeProperties();

    /**
     * Can be used to include only certain properties into the emission (e.g. include only
     * properties that you <b>want</b> to expose). This feature was implemented to avoid big exclude
     * properties tag when we only want very few specific properties.
     *
     * <p>The inclusion rules, will be overruled by the {@link #getExcludeProperties()} rules
     * (e.g. you can write an inclusion rule that applies for multiple
     * properties and then exclude a subset of them).
     * You can therefor can be a bit broader in the inclusion rules and
     * exclude more sensitive ones  in the {@link #getExcludeProperties()} rules.
     *
     * <p>Each value may be globbing, that is, you can write {@code git.commit.user.*} to
     * exclude both the {@code name}, as well as {@code email} properties from being emitted.
     *
     * <p>Please note that the strings here are Java regexes ({@code .*} is globbing,
     * not plain {@code *}).
     * If you have a short list of exclusions you may want to
     * use {@link #getExcludeProperties()}.
     *
     * <p>Defaults to the empty list (= no properties are excluded).
     */
    public abstract ListProperty<String> getIncludeOnlyProperties();

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
        getPropertyPrefix().convention("git");
        getExportDateFormat().convention("yyyy-MM-dd'T'HH:mm:ssZ");
        getExportDateFormatTimeZone().convention(TimeZone.getDefault().getID());
        getSkip().convention(false);
        getExcludeProperties().convention(Collections.emptyList());
        getIncludeOnlyProperties().convention(Collections.emptyList());
        // commitIdGenerationMode
        // replacementProperties
        getUseBranchNameFromBuildEnvironment().convention(true);
        // injectIntoSysProperties
        // projectBuildOutputTimestamp
    }
}
