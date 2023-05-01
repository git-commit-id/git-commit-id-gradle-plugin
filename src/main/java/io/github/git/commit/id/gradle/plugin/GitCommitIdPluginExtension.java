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
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import pl.project13.core.CommitIdPropertiesOutputFormat;
import pl.project13.core.git.GitDescribeConfig;

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

    private static Property<Boolean> boolProp(Project project, boolean convention) {
        return project.getObjects().property(Boolean.class).convention(convention);
    }

    private static Property<String> stringProp(Project project, String convention) {
        return project.getObjects().property(String.class).convention(convention);
    }

    private static Property<Integer> intProp(Project project, int convention) {
        return project.getObjects().property(Integer.class).convention(convention);
    }

    private static Property<Long> longProp(Project project, long convention) {
        return project.getObjects().property(Long.class).convention(convention);
    }

    private static ListProperty<String> emptyStringListProp(Project project) {
        return project.getObjects().listProperty(String.class).convention(Collections.emptyList());
    }

    public GitCommitIdPluginExtension(Project project) {
        // injectAllReactorProjects
        verbose = boolProp(project, false);
        // skipPoms
        generateGitPropertiesFile = boolProp(project, false);
        generateGitPropertiesFilename =
                project.getObjects().fileProperty().convention(
                        () -> project.getBuildDir().toPath().resolve("git.properties").toFile());
        generateGitPropertiesFileWithEscapedUnicode = boolProp(project, false);
        dotGitDirectory =
                project.getObjects().directoryProperty().convention(
                        project.getLayout().getProjectDirectory().dir(".git"));
        gitDescribeConfig =
                project.getObjects()
                        .property(GitDescribeConfig.class)
                        .convention(new GitDescribeConfig());
        abbrevLength = intProp(project, 7);
        generateGitPropertiesFormat =
                project.getObjects()
                        .property(CommitIdPropertiesOutputFormat.class)
                        .convention(CommitIdPropertiesOutputFormat.PROPERTIES);
        propertyPrefix = stringProp(project, "git");
        exportDateFormat = stringProp(project, "yyyy-MM-dd'T'HH:mm:ssZ");
        exportDateFormatTimeZone = stringProp(project, TimeZone.getDefault().getID());
        failOnNoGitDirectory = boolProp(project, true);
        failOnUnableToExtractRepoInfo = boolProp(project, true);
        useNativeGit = boolProp(project, false);
        skip = boolProp(project, false);

        excludeProperties = emptyStringListProp(project);
        includeOnlyProperties = emptyStringListProp(project);
        // commitIdGenerationMode
        // replacementProperties
        evaluateOnCommit = stringProp(project, "HEAD");
        nativeGitTimeoutInMs = longProp(project, 30000L);
        useBranchNameFromBuildEnvironment = boolProp(project, true);
        // injectIntoSysProperties
        offline = boolProp(project, true);
        // projectBuildOutputTimestamp
    }
}
