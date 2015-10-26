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

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.annotation.Task
import net.anshulverma.gradle.release.tasks.CheckCleanWorkspaceTask
import net.anshulverma.gradle.release.tasks.CheckRepositoryBranchTask
import net.anshulverma.gradle.release.tasks.PreReleaseTask
import net.anshulverma.gradle.release.tasks.ReleaseTask
import net.anshulverma.gradle.release.tasks.ShowPublishInfoTask
import net.anshulverma.gradle.release.tasks.SnapshotTask
import net.anshulverma.gradle.release.tasks.TaskRegistry
import net.anshulverma.gradle.release.tasks.VersionProjectTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Registers the plugin's tasks.
 *
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class ReleasePlugin implements Plugin<Project> {

  @Override
  void apply(def Project project) {
    if (project.plugins.hasPlugin('java') || project.plugins.hasPlugin('groovy')) {
      [
          ShowPublishInfoTask,
          ReleaseTask,
          SnapshotTask,
          PreReleaseTask,
          CheckCleanWorkspaceTask,
          CheckRepositoryBranchTask,
          VersionProjectTask
      ].each { taskType ->
        project.tasks.create(taskType.getAnnotation(Task).value().taskName, taskType)
      }
    }

    project.afterEvaluate { evaluatedProject ->
      TaskRegistry.INSTANCE.resolveDependencies(evaluatedProject)
    }
  }
}
