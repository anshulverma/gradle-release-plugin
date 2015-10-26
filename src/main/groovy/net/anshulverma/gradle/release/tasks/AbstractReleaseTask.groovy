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
package net.anshulverma.gradle.release.tasks

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.annotation.Dependent
import net.anshulverma.gradle.release.annotation.DependsOn
import net.anshulverma.gradle.release.annotation.Task
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
abstract class AbstractReleaseTask extends DefaultTask {

  final TaskType taskType

  protected AbstractReleaseTask() {
    Task task = getClass().getAnnotation(Task)
    taskType = task.value()
    description = task.description()

    TaskRegistry.INSTANCE.register(this, taskType, getDependencies(), getDependent())

    group = 'Release'
  }

  TaskType[] getDependencies() {
    DependsOn dependsOn = getClass().getAnnotation(DependsOn)
    if (dependsOn != null) {
      return dependsOn.value()
    }
    return []
  }

  TaskType getDependent() {
    Dependent dependent = getClass().getAnnotation(Dependent)
    if (dependent) {
      return dependent.value()
    }
    return null
  }

  @TaskAction
  final run() {
    def project = getProject()
    logger.debug('running task type {}', taskType)
    execute(project)
  }

  abstract protected execute(Project project)
}
