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
import javax.inject.Inject;
import org.gradle.api.provider.ListProperty;


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
 * <p>This Extension exposes all configuration options to configure filtering of any
 * properties that may be generated as part of the plugin execution.
 * Do not want to expose hte git.commit.user.email? No problem! Use the filtering
 * to trim down all the properties you do not need, or do not want to be generated.
 * As a plus, filtering may also make the plugin execution faster by a concept that is called
 * 'selective' running. Essentially behind the back the plugin will only ask
 * your git repository about the properties you want to generate.
 * If you have them filtered out, the step of gathering the information is automatically skipped.
 *
 * <p>This extension is generally only made available through the
 * {@link GitCommitIdPluginExtension}.
 * If you want to learn about the plugin, refer to {@link GitCommitIdPlugin}.
 * If you are interested in the task that generates the actual information refer to
 * {@link GitCommitIdPluginGenerationTask}.
 */
public abstract class GitCommitIdPluginFilterSettingsExtension {
    /**
     * Name of the extension how it's made available to the end-user as
     * DSL like configuration in the {@code build.gradle}:
     * <pre>
     * gitCommitId {
     *     filterSettings {
     *         excludeProperties.set([...]])
     *     }
     * }
     * </pre>
     * As may notice this extension is made available as "nested" extension
     * of the {@link GitCommitIdPluginExtension}.
     */
    public static final String NAME = "filterSettings";

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
     * Setup the default values / conventions for the GitCommitIdPluginOutputSettingsExtension.
     */
    @Inject
    public GitCommitIdPluginFilterSettingsExtension() {
        getExcludeProperties().convention(Collections.emptyList());
        getIncludeOnlyProperties().convention(Collections.emptyList());
    }
}
