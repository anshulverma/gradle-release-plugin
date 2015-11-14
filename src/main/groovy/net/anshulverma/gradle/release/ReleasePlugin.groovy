/**
 * Copyright 2015 Anshul Verma. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.anshulverma.gradle.release

import net.anshulverma.gradle.release.annotation.Task
import net.anshulverma.gradle.release.tasks.CheckCleanWorkspaceTask
import net.anshulverma.gradle.release.tasks.CheckReleaseTask
import net.anshulverma.gradle.release.tasks.CheckRepositoryBranchTask
import net.anshulverma.gradle.release.tasks.PreReleaseTask
import net.anshulverma.gradle.release.tasks.PreSnapshotTask
import net.anshulverma.gradle.release.tasks.ReleaseTask
import net.anshulverma.gradle.release.tasks.ShowPublishInfoTask
import net.anshulverma.gradle.release.tasks.SnapshotTask
import net.anshulverma.gradle.release.tasks.TaskRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Registers the plugin's tasks.
 *
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ReleasePlugin implements Plugin<Project> {

  private final ReleasePluginHelper helper = new ReleasePluginHelper()

  @Override
  void apply(def Project project) {
    project.allprojects {
      helper.setupVersion(it)
    }

    project.afterEvaluate {
      if (project.subprojects) {
        project.subprojects.each { apply_plugin(it) }
      } else {
        apply_plugin(project)
      }
    }
  }

  void apply_plugin(Project project) {
    if (project.plugins.hasPlugin('java') || project.plugins.hasPlugin('groovy')) {
      [
          ShowPublishInfoTask,
          ReleaseTask,
          PreReleaseTask,
          SnapshotTask,
          PreSnapshotTask,
          CheckReleaseTask,
          CheckCleanWorkspaceTask,
          CheckRepositoryBranchTask
      ].each { taskType ->
        project.tasks.create(taskType.getAnnotation(Task).value().taskName, taskType)
      }
    }

    helper.configurePublications(project)

    project.afterEvaluate { evaluatedProject ->
      TaskRegistry.INSTANCE.resolveDependencies(evaluatedProject)
    }
  }
}
