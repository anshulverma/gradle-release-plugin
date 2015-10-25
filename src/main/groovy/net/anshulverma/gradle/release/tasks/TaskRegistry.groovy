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

import groovy.transform.TypeChecked

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
class TaskRegistry {

  final static TaskRegistry INSTANCE = new TaskRegistry()

  final Map<TaskType, TaskContext> taskMap = [:]

  def register(AbstractTask task, TaskType taskType, TaskType[] dependencies) {
    def taskContext = TaskContext.builder()
                                 .task(task)
                                 .type(taskType)
                                 .dependencies(dependencies)
                                 .build()
    if (taskMap[taskType]) {
      throw new IllegalArgumentException(
          "task of type $taskType has already been registered with context ${taskMap[taskType]}")
    }
    taskMap << [(taskType): taskContext]
  }

  def resolveDependencies() {
    taskMap.each { TaskType taskType, TaskContext taskContext ->
      if (!taskContext.dependencies) {
        return
      }
      taskContext.dependencies.each { TaskType dependencyType ->
        TaskContext dependencyContext = taskMap[dependencyType]
        if (!dependencyContext) {
          throw new IllegalStateException(
              "unable to resolve dependency type $dependencyType for $taskContext")
        }
        taskContext.task.dependsOn(dependencyContext.task)
      }
    }
  }
}
