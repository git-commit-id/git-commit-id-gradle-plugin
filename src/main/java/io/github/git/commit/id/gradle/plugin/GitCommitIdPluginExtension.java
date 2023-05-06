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
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import pl.project13.core.CommitIdPropertiesOutputFormat;
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
     * Configuration option to enable or disable more verbose information during the
     * execution of the plugin.
     * To enable such more debugging messages, set this to {@code true}.
     * By default, this setting is disabled (set to {@code false}).
     */
    public abstract Property<Boolean> getVerbose();

    /**
     * The information on what git revision your project is built with must come from the
     * git itself.
     * If you'd like to tell the plugin where your {@code .git} directory is, use this setting,
     * by default this plugin assumes {@code getProjectDirectory()/.git}.
     */
    // @InputDirectory
    public abstract DirectoryProperty getDotGitDirectory();

    /**
     * The following {@code gitDescribeConfig} below is optional and can be leveraged as a
     * really powerful versioning helper. If you are not familiar with
     * <a href="https://git-scm.com/docs/git-describe">git-describe</a>
     * it is highly recommended to go through this part of the documentation. More advanced
     * users can most likely skip the explanations in this section, as it just explains the
     * same options that git provides.
     * In summary {@code gitDescribe} can translate your current branch
     * into a human-readable name like {@code v1.0.4-14-g2414721}
     * that represents all relevant version information.
     *
     * <p>Refer to {@link GitDescribeConfig} for options that can be passed towards git-describe.
     * The result of this configuration is exposed as
     * {@link #getPropertyPrefix()}{@code .commit.id.describe}.
     */
    public abstract Property<GitDescribeConfig> getGitDescribeConfig();

    /**
     * By default git uses 40 character long SHA-1 hashes to represent commit hashes.
     * In most cases it is however already sufficient to display only a few characters
     * at the front of this commit hash to get an unique enough identifier of the commit hash.
     * By default git chooses the first 7 characters of the hash, which is then called abbreviation.
     *
     * <p>The plugin therefore sets the abbreviation to {@code 7} by default.
     * Please note that larger projects might want to increase this to eight to ten characters
     * to avoid a collision (e.g. an abbreviated hash would not be unique anymore and could
     * point to two different commits in the database).
     *
     * <p>Refer to <a href="https://git-scm.com/book/en/v2/Git-Tools-Revision-Selection">Git Tools - Revision Selection</a>
     * for more details.
     *
     */
    public abstract Property<Integer> getAbbrevLength();

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
     * Control whether the plugin should fail when a .git directory cannot be found.
     * The directory can be configured by {@link #getDotGitDirectory()}.
     * When set to {@code false} and no {@code .git} directory is found the plugin
     * will skip execution.
     *
     * <p>Defaults to {@code true}, so a missing {@code .git} directory may cause a build failure.
     */
    public abstract Property<Boolean> getFailOnNoGitDirectory();

    /**
     * Control whether the plugin should fail the build if it's unable to
     * obtain enough data for a complete run (e.g. gather all requested information).
     *
     * <p>Defaults to {@code true}, so missing data may cause a build failure.
     */
    public abstract Property<Boolean> getFailOnUnableToExtractRepoInfo();

    /**
     * This plugin ships with custom {@code jgit} implementation that is being used to obtain all
     * relevant information. If set to {@code true} this plugin will use the
     * native {@code git} binary instead of the custom {@code jgit} implementation.
     *
     * <p>Although this should usually give your build some performance boost, it may randomly
     * break if you upgrade your git version if it decides to print information in a different
     * format suddenly. As rule of thumb, keep using the default {@code jgit} implementation (keep
     * this {@code false}) until you notice performance problems within your build (usually when you
     * have *hundreds* of gradle projects).
     *
     * <p>To not get your build stuck forever, this plugin also has an option to configure a
     * maximum timeout to wait for any native command. Refer to {@link #getNativeGitTimeoutInMs()}.
     */
    public abstract Property<Boolean> getUseNativeGit();

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
     * Allow to tell the plugin what commit should be used as reference to
     * generate the properties from.
     *
     * <p>In general this property can be set to something generic like {@code HEAD^1} or point to a
     * branch or tag-name. To support any kind or use-case this configuration can also be set
     * to an entire commit-hash or it's abbreviated version.
     *
     * <p>A use-case for this feature can be found in
     * <a href="https://github.com/git-commit-id/git-commit-id-maven-plugin/issues/338">here</a>.
     *
     * <p>Please note that for security purposes not all references might
     * be allowed as configuration. If you have a specific use-case that is currently
     * not white listed feel free to file an issue.
     */
    public abstract Property<String> getEvaluateOnCommit();

    /**
     * Allow to specify a timeout (in milliseconds) for fetching information with the native
     * Git executable. This option might come in handy in cases where fetching information
     * about the repository with the native Git executable does not terminate.
     *
     * <p>Note: This option will only be taken into consideration when using the native git
     * executable ({@link #getUseNativeGit()} is set to {@code true}).
     *
     * <p>By default this timeout is set to 30000 (30 seconds).
     */
    public abstract Property<Long> getNativeGitTimeoutInMs();

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
     * The plugin can generate certain properties that represents the count of commits
     * that your local branch is ahead or behind in perspective to the remote branch.
     *
     * <p>When your branch is "ahead" it means your local branch has committed changes that are not
     * pushed yet to the remote branch.
     * When your branch is "behind" it means there are commits in the remote branch that are not yet
     * integrated into your local branch.
     *
     * <p>This configuration allows you to control if the plugin should somewhat ensure
     * that such properties are more accurate. More accurate means that the plugin will perform a
     * {@code git fetch} before the properties are calculated.
     * Certainly a {@code git fetch} is an operation that may alter your local git repository
     * and thus the plugin will operate not perform such operation (offline is set to {@code true}).
     * If you however desire more accurate properties you may want to set this to {@code false}.
     */
    public abstract Property<Boolean> getOffline();

    @Inject
    public ProjectLayout getProjectLayout() {
        throw new IllegalStateException("Should have been injected!");
    }

    /**
     * Setup the default values / conventions for the GitCommitIdPluginExtension.
     */
    @Inject
    public GitCommitIdPluginExtension() {
        // injectAllReactorProjects
        getVerbose().convention(false);
        // skipPoms
        getDotGitDirectory().convention(
            getProjectLayout().getProjectDirectory().dir(".git"));
        getGitDescribeConfig().convention(new GitDescribeConfig());
        getAbbrevLength().convention(7);
        getPropertyPrefix().convention("git");
        getExportDateFormat().convention("yyyy-MM-dd'T'HH:mm:ssZ");
        getExportDateFormatTimeZone().convention(TimeZone.getDefault().getID());
        getFailOnNoGitDirectory().convention(true);
        getFailOnUnableToExtractRepoInfo().convention(true);
        getUseNativeGit().convention(false);
        getSkip().convention(false);

        getExcludeProperties().convention(Collections.emptyList());
        getIncludeOnlyProperties().convention(Collections.emptyList());
        // commitIdGenerationMode
        // replacementProperties
        getEvaluateOnCommit().convention("HEAD");
        getNativeGitTimeoutInMs().convention(30000L);
        getUseBranchNameFromBuildEnvironment().convention(true);
        // injectIntoSysProperties
        getOffline().convention(true);
        // projectBuildOutputTimestamp
    }
}
