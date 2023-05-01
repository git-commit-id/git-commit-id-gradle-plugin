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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import pl.project13.core.CommitIdGenerationMode;
import pl.project13.core.CommitIdPropertiesOutputFormat;
import pl.project13.core.GitCommitIdExecutionException;
import pl.project13.core.git.GitDescribeConfig;
import pl.project13.core.log.LogInterface;
import pl.project13.core.util.BuildFileChangeListener;


@CacheableTask
public class GitCommitIdPluginGenerationTask extends DefaultTask {
  @Inject
  public Project getProject() {
    throw new IllegalStateException();
  }

  private GitCommitIdPluginExtension getExtension() {
    return getProject().getExtensions().findByType(GitCommitIdPluginExtension.class);
  }

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public DirectoryProperty getInput() {
    return getExtension().dotGitDirectory;
  }

  @OutputFile
  public RegularFileProperty getOutput() {
    return getExtension().generateGitPropertiesFilename;
  }

  @TaskAction
  public void runTheTask() {
    GitCommitIdPluginExtension extension = getExtension();
    boolean verbose = extension.verbose.get();
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

    final pl.project13.core.GitCommitIdPlugin.Callback cb = new pl.project13.core.GitCommitIdPlugin.Callback() {
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
        return extension.exportDateFormat.get();
      }

      @Nonnull
      @Override
      public String getDateFormatTimeZone() {
        return extension.exportDateFormatTimeZone.get();
      }

      @Nonnull
      @Override
      public String getPrefixDot() {
        String trimmedPrefix = extension.propertyPrefix.get().trim();
        return trimmedPrefix.equals("") ? "" : trimmedPrefix + ".";
      }

      @Override
      public List<String> getExcludeProperties() {
        return extension.excludeProperties.get();
      }

      @Override
      public List<String> getIncludeOnlyProperties() {
        return extension.includeOnlyProperties.get();
      }

      @Nullable
      @Override
      public Date getReproducibleBuildOutputTimestamp() throws GitCommitIdExecutionException {
        // TODO
        return new Date();
      }

      @Override
      public boolean useNativeGit() {
        return extension.useNativeGit.get();
      }

      @Override
      public long getNativeGitTimeoutInMs() {
        return extension.nativeGitTimeoutInMs.get();
      }

      @Override
      public int getAbbrevLength() {
        return extension.abbrevLength.get();
      }

      @Override
      public GitDescribeConfig getGitDescribe() {
        return extension.gitDescribeConfig.get();
      }

      @Override
      public CommitIdGenerationMode getCommitIdGenerationMode() {
        return CommitIdGenerationMode.FULL;
      }

      @Override
      public boolean getUseBranchNameFromBuildEnvironment() {
        return extension.useBranchNameFromBuildEnvironment.get();
      }

      @Override
      public boolean isOffline() {
        return extension.offline.get();
      }

      @Override
      public String getEvaluateOnCommit() {
        // TODO: validate commit string!!!
        return extension.evaluateOnCommit.get();
      }

      @Override
      public File getDotGitDirectory() {
        return extension.dotGitDirectory.get().getAsFile();
      }

      @Override
      public boolean shouldGenerateGitPropertiesFile() {
        return extension.generateGitPropertiesFile.get();
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
        return extension.generateGitPropertiesFormat.get();
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
        return extension.generateGitPropertiesFilename.get().getAsFile();
      }

      @Override
      public Charset getPropertiesSourceCharset() {
        Charset sourceCharset = StandardCharsets.UTF_8;
        String sourceEncoding = null; // TODO project.getProperties().getProperty("project.build.sourceEncoding");
        if (null != sourceEncoding) {
          sourceCharset = Charset.forName(sourceEncoding);
        } else {
          sourceCharset = Charset.defaultCharset();
        }
        return sourceCharset;
      }

      @Override
      public boolean shouldPropertiesEscapeUnicode() {
        return extension.generateGitPropertiesFileWithEscapedUnicode.get();
      }
    };

    try {
      Properties properties = null;
      pl.project13.core.GitCommitIdPlugin.runPlugin(cb, properties);

      // getLogger().info("GitCommitIdPlugin says: '%s'", properties);
    } catch (GitCommitIdExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
