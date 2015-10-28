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

import groovy.transform.TypeChecked
import net.anshulverma.gradle.release.annotation.Task
import net.anshulverma.gradle.release.info.ReleaseInfoFactory
import net.anshulverma.gradle.release.tasks.AbstractReleaseTask
import net.anshulverma.gradle.release.tasks.TaskRegistry
import net.anshulverma.gradle.release.tasks.TaskType
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
abstract class AbstractSpecificationTest extends Specification {

  protected Project newProject(String name = 'testProject') {
    Project project = ProjectBuilder.builder()
                                    .withName(name)
                                    .build()
    project.apply plugin: 'java'
    project
  }

  protected <T extends AbstractReleaseTask> T newTask(Class<T> taskClass, Project project = newProject()) {
    TaskType taskType = taskClass.getAnnotation(Task).value()
    project.tasks.create(taskType.taskName, taskClass)
    project.getTasksByName(taskType.taskName, true)[0] as T
  }

  def setup() {
    TaskRegistry.INSTANCE.reset()
    ReleaseInfoFactory.INSTANCE.reset()
  }
}
