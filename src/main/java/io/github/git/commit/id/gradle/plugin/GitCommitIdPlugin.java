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

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GitCommitIdPlugin implements Plugin<Project> {
    public static final String GIT_COMMIT_ID_EXTENSION_NAME = "gitCommitId";
    public static final String GIT_COMMIT_ID_TASK_NAME = "gitCommitIdGenerationTask";

    public void apply(Project project) {
        var extension = project.getExtensions().create(
                GIT_COMMIT_ID_EXTENSION_NAME, GitCommitIdPluginExtension.class, project);
        var task = project.getTasks().create(
                GIT_COMMIT_ID_TASK_NAME, GitCommitIdPluginGenerationTask.class);
        task.onlyIf(ignore -> extension.skip.get() == false);
    }
}