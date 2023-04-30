package io.github.git.commit.id.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import pl.project13.core.CommitIdPropertiesOutputFormat;
import pl.project13.core.git.GitDescribeConfig;

import java.util.Collections;
import java.util.TimeZone;

public class GitCommitIdPluginExtension {
    Property<Boolean> verbose;
    Property<Boolean> generateGitPropertiesFile;
    @OutputFile
    RegularFileProperty generateGitPropertiesFilename;
    Property<Boolean> generateGitPropertiesFileWithEscapedUnicode;
    @InputDirectory
    DirectoryProperty dotGitDirectory;
    Property<GitDescribeConfig> gitDescribeConfig;
    Property<Integer> abbrevLength;
    Property<CommitIdPropertiesOutputFormat> generateGitPropertiesFormat;
    Property<String> propertyPrefix;
    Property<String> exportDateFormat;
    Property<String> exportDateFormatTimeZone;
    Property<Boolean> failOnNoGitDirectory;
    Property<Boolean> failOnUnableToExtractRepoInfo;
    Property<Boolean> useNativeGit;
    Property<Boolean> skip;
    ListProperty<String> excludeProperties;
    ListProperty<String> includeOnlyProperties;
    Property<String> evaluateOnCommit;
    Property<Long> nativeGitTimeoutInMs;
    Property<Boolean> useBranchNameFromBuildEnvironment;
    Property<Boolean> offline;

    public GitCommitIdPluginExtension(Project project) {
        // injectAllReactorProjects
        verbose = project.getObjects().property(Boolean.class).convention(false);
        // skipPoms
        generateGitPropertiesFile = project.getObjects().property(Boolean.class).convention(false);
        generateGitPropertiesFilename = project.getObjects().fileProperty().convention(
                () -> project.getBuildDir().toPath().resolve("git.properties").toFile());
        generateGitPropertiesFileWithEscapedUnicode = project.getObjects().property(Boolean.class).convention(false);
        dotGitDirectory = project.getObjects().directoryProperty().convention(
                project.getLayout().getProjectDirectory().dir(".git"));
        gitDescribeConfig = project.getObjects().property(GitDescribeConfig.class).convention(new GitDescribeConfig());
        abbrevLength = project.getObjects().property(Integer.class).convention(7);
        generateGitPropertiesFormat = project.getObjects().property(CommitIdPropertiesOutputFormat.class).convention(CommitIdPropertiesOutputFormat.PROPERTIES);
        propertyPrefix = project.getObjects().property(String.class).convention("git");
        exportDateFormat = project.getObjects().property(String.class).convention("yyyy-MM-dd'T'HH:mm:ssZ");
        exportDateFormatTimeZone = project.getObjects().property(String.class).convention(TimeZone.getDefault().getID());
        failOnNoGitDirectory = project.getObjects().property(Boolean.class).convention(true);
        failOnUnableToExtractRepoInfo = project.getObjects().property(Boolean.class).convention(true);
        useNativeGit = project.getObjects().property(Boolean.class).convention(false);
        skip = project.getObjects().property(Boolean.class).convention(false);

        excludeProperties = project.getObjects().listProperty(String.class).convention(Collections.emptyList());
        includeOnlyProperties = project.getObjects().listProperty(String.class).convention(Collections.emptyList());
        // commitIdGenerationMode
        // replacementProperties
        evaluateOnCommit = project.getObjects().property(String.class).convention("HEAD");
        nativeGitTimeoutInMs = project.getObjects().property(Long.class).convention(30000L);
        useBranchNameFromBuildEnvironment = project.getObjects().property(Boolean.class).convention(true);
        // injectIntoSysProperties
        offline = project.getObjects().property(Boolean.class).convention(true);
        // projectBuildOutputTimestamp
    }
}
