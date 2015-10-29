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
package net.anshulverma.gradle.release.common

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.tasks.TaskType
import org.gradle.api.Project

/**
 * Allows us to not print messages if snapshot or release tasks are not being executed
 *
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class Logger {

  private static final OWN_TASKS =
      [
          TaskType.RELEASE,
          TaskType.PRE_RELEASE,
          TaskType.SNAPSHOT,
          TaskType.PRE_SNAPSHOT,
          TaskType.CHECK_RELEASE,
          TaskType.SHOW_PUBLISH_INFO,
          TaskType.CHECK_CLEAN_WORKSPACE,
          TaskType.CHECK_REPOSITORY_BRANCH,
          TaskType.VERSION_PROJECT
      ]

  static warn(Project project, String message) {
    logMessage(project, message) { log.warn(it) }
  }

  private static logMessage(Project project, String message, Closure closure) {
    if (isExecutingOwnTask(project)) {
      closure(message)
    } else {
      log.info(message)
    }
  }

  private static boolean isExecutingOwnTask(Project project) {
    OWN_TASKS.find { taskType ->
      project.gradle.startParameter.taskNames.contains(taskType.taskName)
    }
  }
}
