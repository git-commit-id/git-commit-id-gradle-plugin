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


import java.util.TimeZone;
import javax.inject.Inject;
import org.gradle.api.file.ProjectLayout;
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
 * <p>This Extension exposes all configuration options to configure the formatting
 * any properties (also the ones that may be exported into an output
 * file as part of the plugin execution).
 * This extension is generally only made available through the {@link GitCommitIdPluginExtension}.
 * If you want to learn about the plugin, refer to {@link GitCommitIdPlugin}.
 * If you are interested in the task that generates the actual information refer to
 * {@link GitCommitIdPluginGenerationTask}.
 */
public abstract class GitCommitIdPluginFormatSettingsExtension {
    /**
     * Name of the extension how it's made available to the end-user as
     * DSL like configuration in the {@code build.gradle}:
     * <pre>
     * gitCommitId {
     *     formatSettings {
     *         propertyPrefix.set("git")
     *     }
     * }
     * </pre>
     * As may notice this extension is made available as "nested" extension
     * of the {@link GitCommitIdPluginExtension}.
     */
    public static final String NAME = "formatSettings";

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
     * <p>Refer to {@link #getDateFormatTimeZone()} if you want to change the time-zone.
     */
    public abstract Property<String> getDateFormat();

    /**
     * Allows to configure the time zone which is utilized for {@link #getDateFormat()}.
     *
     * <p>Defaults to {@code java.util.TimeZone.getDefault().getID()}.
     * Allows various formats of timezone configuration
     * (e.g. 'America/Los_Angeles', 'GMT+10', 'PST').
     * As a general warning try to avoid three-letter time zone IDs because the same
     * abbreviation are often used for multiple time zones.
     */
    public abstract Property<String> getDateFormatTimeZone();

    @Inject
    public ProjectLayout getProjectLayout() {
        throw new IllegalStateException("Should have been injected!");
    }

    /**
     * Setup the default values / conventions for the GitCommitIdPluginFormatSettingsExtension.
     */
    @Inject
    public GitCommitIdPluginFormatSettingsExtension() {
        getPropertyPrefix().convention("git");
        getDateFormat().convention("yyyy-MM-dd'T'HH:mm:ssZ");
        getDateFormatTimeZone().convention(TimeZone.getDefault().getID());
    }
}
