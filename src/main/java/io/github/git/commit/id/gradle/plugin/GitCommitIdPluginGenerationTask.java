package io.github.git.commit.id.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.*;
import pl.project13.core.CommitIdGenerationMode;
import pl.project13.core.GitCommitIdExecutionException;
import pl.project13.core.git.GitDescribeConfig;
import pl.project13.core.log.LogInterface;
import pl.project13.core.util.BuildFileChangeListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

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
      public String getPropertiesOutputFormat() {
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
      public String getGenerateGitPropertiesFilename() {
        // TODO Eh why is this a String and not a File?
        return extension.generateGitPropertiesFilename.get().getAsFile().toString();
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
