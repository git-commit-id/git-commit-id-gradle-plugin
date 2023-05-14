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

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import pl.project13.core.CommitIdGenerationMode;
import pl.project13.core.CommitIdPropertiesOutputFormat;
import pl.project13.core.GitCommitIdExecutionException;
import pl.project13.core.GitCommitIdPlugin;
import pl.project13.core.GitCommitPropertyConstant;
import pl.project13.core.git.GitDescribeConfig;
import pl.project13.core.log.LogInterface;
import pl.project13.core.util.BuildFileChangeListener;

/**
 * The task that generates the "git" information.
 * If you wish to change any configuration you may refer to the
 * {@link GitCommitIdPluginExtension} class in the API documentation.
 * If you are interested in the plugin itself you may refer to {@link GitCommitIdPlugin}.
 */
@CacheableTask
public class GitCommitIdPluginGenerationTask extends DefaultTask {
    /**
     * Name of the task how it's made available to the end-user's
     * task execution graph.
     */
    public static final String NAME = "gitCommitIdGenerationTask";
    public static final String PLUGIN_EXECUTION_MESSAGE =
        "Executing GitCommitIdPlugin to gather relevant properties";
    private HashMap<String, String> lastGeneratedOutput;

    /**
     * The {@link GitCommitIdPluginExtension} that serves as configuration of the plugin / task.
     *
     * @return The {@link GitCommitIdPluginExtension}
     */
    @Internal
    public GitCommitIdPluginExtension getGitCommitIdPluginExtension() {
        return getProject().getExtensions().findByType(GitCommitIdPluginExtension.class);
    }

    @Internal
    public GitCommitIdPluginOutputSettingsExtension getGitCommitIdPluginOutputSettingsExtension() {
        return ((ExtensionAware) getGitCommitIdPluginExtension()).getExtensions()
            .findByType(GitCommitIdPluginOutputSettingsExtension.class);
    }

    @Internal
    public GitCommitIdPluginGitSettingsExtension getGitCommitIdPluginGitSettingsExtension() {
        return ((ExtensionAware) getGitCommitIdPluginExtension()).getExtensions()
            .findByType(GitCommitIdPluginGitSettingsExtension.class);
    }

    @Internal
    public GitCommitIdPluginFormatSettingsExtension getGitCommitIdPluginFormatSettingsExtension() {
        return ((ExtensionAware) getGitCommitIdPluginExtension()).getExtensions()
            .findByType(GitCommitIdPluginFormatSettingsExtension.class);
    }

    @Internal
    public GitCommitIdPluginFilterSettingsExtension getGitCommitIdPluginFilterSettingsExtension() {
        return ((ExtensionAware) getGitCommitIdPluginExtension()).getExtensions()
            .findByType(GitCommitIdPluginFilterSettingsExtension.class);
    }

    /**
     * Since we are generating "git" information this task needs to specify the git-directory
     * as input. The input can then be used by gradle to determine if the task is "up-to-date"
     * or needs to re-run. As general expectation we can assume that the task should be "up-to-date"
     * when there are no changes in the underlying git directory.
     *
     * @return The git directory that shall be used to generate the "git" information
     */
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public DirectoryProperty getInput() {
        return getGitCommitIdPluginGitSettingsExtension().getDotGitDirectory();
    }

    /**
     * The plugin allows to generate a "properties" file. For gradle's "up-to-date" checks
     * we need to specify it as output. The file is optional, but gradle can handle that.
     *
     * @return
     *     The optional "properties" file that can be used to store the generated
     *     properties in a file.
     */
    @OutputFile
    public RegularFileProperty getOutput() {
        return getGitCommitIdPluginOutputSettingsExtension().getOutputFile();
    }

    /**
     * Attempt to support up-to-date checks with the generated properties.
     * A similar concept is used in {@link org.gradle.api.tasks.WriteProperties}.
     *
     * @return
     *      The properties that may have been generated by previous runs, or if not up-to-date
     *      recalculates the properties by execution the plugin again.
     */
    @Input
    public HashMap<String, String> getGeneratedProperties() {
        if (lastGeneratedOutput != null) {
            return lastGeneratedOutput;
        }
        lastGeneratedOutput = runThePlugin();
        return lastGeneratedOutput;
    }

    /**
     * The task action that ties it all together and runs the underlying logic of gathering the data
     * and exporting it to the relevant locations.
     */
    @TaskAction
    public void runTheTask() {
        getProject()
          .getExtensions()
          .getExtraProperties()
            .set("gitProperties", getGeneratedProperties());
    }

    private HashMap<String, String> runThePlugin() {
        getLogger().debug(PLUGIN_EXECUTION_MESSAGE);
        GitCommitIdPluginExtension extension = getGitCommitIdPluginExtension();
        boolean verbose = extension.getVerbose().get();
        final LogInterface log = new LogInterface() {
            @Override
            public void debug(String msg) {
                if (verbose) {
                    getLogger().debug(msg);
                }
            }

            @Override
            public void info(String msg) {
                if (verbose) {
                    getLogger().info(msg);
                }
            }

            @Override
            public void warn(String msg) {
                if (verbose) {
                    getLogger().warn(msg);
                }
            }

            @Override
            public void error(String msg) {
                if (verbose) {
                    getLogger().error(msg);
                }
            }

            @Override
            public void error(String msg, Throwable t) {
                if (verbose) {
                    getLogger().error(msg, t);
                }
            }
        };

        final GitCommitIdPlugin.Callback cb = new GitCommitIdPlugin.Callback() {
            @Override
            public Map<String, String> getSystemEnv() {
                return System.getenv();
            }

            @Override
            public Supplier<String> supplyProjectVersion() {
                return () -> getProject().getVersion().toString();
            }

            @Nonnull
            @Override
            public LogInterface getLogInterface() {
                return log;
            }

            @Nonnull
            @Override
            public String getDateFormat() {
                return getGitCommitIdPluginFormatSettingsExtension()
                    .getDateFormat().get();
            }

            @Nonnull
            @Override
            public String getDateFormatTimeZone() {
                return getGitCommitIdPluginFormatSettingsExtension()
                    .getDateFormatTimeZone().get();
            }

            @Nonnull
            @Override
            public String getPrefixDot() {
                String trimmedPrefix = getGitCommitIdPluginFormatSettingsExtension()
                    .getPropertyPrefix().get().trim();
                return trimmedPrefix.equals("") ? "" : trimmedPrefix + ".";
            }

            @Override
            public List<String> getExcludeProperties() {
                return getGitCommitIdPluginFilterSettingsExtension()
                    .getExcludeProperties().get();
            }

            @Override
            public List<String> getIncludeOnlyProperties() {
                return getGitCommitIdPluginFilterSettingsExtension()
                  .getIncludeOnlyProperties().get();
            }

            @Nullable
            @Override
            public Date getReproducibleBuildOutputTimestamp() throws GitCommitIdExecutionException {
                // TODO
                return new Date();
            }

            @Override
            public boolean useNativeGit() {
                return getGitCommitIdPluginGitSettingsExtension().getShouldUseNativeGit().get();
            }

            @Override
            public long getNativeGitTimeoutInMs() {
                return getGitCommitIdPluginGitSettingsExtension().getNativeGitTimeoutInMs().get();
            }

            @Override
            public int getAbbrevLength() {
                return getGitCommitIdPluginGitSettingsExtension().getAbbrevLength().get();
            }

            @Override
            public GitDescribeConfig getGitDescribe() {
                return getGitCommitIdPluginGitSettingsExtension().getGitDescribeConfig().get();
            }

            @Override
            public CommitIdGenerationMode getCommitIdGenerationMode() {
                return CommitIdGenerationMode.FULL;
            }

            @Override
            public boolean getUseBranchNameFromBuildEnvironment() {
                return getGitCommitIdPluginGitSettingsExtension()
                  .getShouldUseBranchNameFromBuildEnvironment().get();
            }

            @Override
            public boolean isOffline() {
                return getGitCommitIdPluginGitSettingsExtension().getShouldStayOffline().get();
            }

            @Override
            public String getEvaluateOnCommit() {
                return getGitCommitIdPluginGitSettingsExtension().getEvaluateOnCommit().get();
            }

            @Override
            public File getDotGitDirectory() {
                return getGitCommitIdPluginGitSettingsExtension()
                  .getDotGitDirectory().get().getAsFile();
            }

            @Override
            public boolean shouldGenerateGitPropertiesFile() {
                return getGitCommitIdPluginOutputSettingsExtension()
                  .getShouldGenerateOutputFile().get();
            }

            @Override
            public void performPublishToAllSystemEnvironments(Properties properties) {
                // TODO
            }

            @Override
            public void performPropertiesReplacement(Properties properties) {
                // TODO
            }

            @Override
            public CommitIdPropertiesOutputFormat getPropertiesOutputFormat() {
                return getGitCommitIdPluginOutputSettingsExtension()
                  .getOutputFormat()
                  .get();
            }

            @Override
            public BuildFileChangeListener getBuildFileChangeListener() {
                return file -> {
                    // TODO
                };
            }

            @Override
            public String getProjectName() {
                return getProject().getName();
            }

            @Override
            public File getProjectBaseDir() {
                return getProject().getRootDir();
            }

            @Override
            public File getGenerateGitPropertiesFile() {
                return getGitCommitIdPluginOutputSettingsExtension()
                  .getOutputFile()
                  .get().getAsFile();
            }

            @Override
            public Charset getPropertiesSourceCharset() {
                Charset sourceCharset = StandardCharsets.UTF_8;
                // TODO project.getProperties().getProperty("project.build.sourceEncoding");
                String sourceEncoding = null;
                if (null != sourceEncoding) {
                    sourceCharset = Charset.forName(sourceEncoding);
                } else {
                    sourceCharset = Charset.defaultCharset();
                }
                return sourceCharset;
            }

            @Override
            public boolean shouldPropertiesEscapeUnicode() {
                return getGitCommitIdPluginOutputSettingsExtension()
                  .getShouldEscapedUnicodeForPropertiesOutput().get();
            }
        };

        try {
            Properties properties = new Properties();
            pl.project13.core.GitCommitIdPlugin.runPlugin(cb, properties);

            // Convert it to a hashmap, excluding build_time
            var prefix = getGitCommitIdPluginFormatSettingsExtension().getPropertyPrefix().get();
            HashMap<String, String> hm = new HashMap<>();
            for (Map.Entry<Object, Object> e : properties.entrySet()) {
                String key = (String) e.getKey();
                // DO NOT EXPOSE BUILD_TIME, otherwise up-to-date checks will not work
                if (key.startsWith(prefix) && key.endsWith(GitCommitPropertyConstant.BUILD_TIME)) {
                    hm.put(key, "");
                } else {
                    hm.put(key, (String) e.getValue());
                }
            }
            return hm;
        } catch (GitCommitIdExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
