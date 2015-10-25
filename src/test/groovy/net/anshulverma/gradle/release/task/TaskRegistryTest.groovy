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
package net.anshulverma.gradle.release.task

import net.anshulverma.gradle.release.tasks.TaskRegistry
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class TaskRegistryTest extends Specification {

  private static final Closure<Boolean> TASK_FITER = { dependency ->
    dependency.class in Collection
  }

  def 'test dependency resolution -- happy path'() {
    given:
      def project = ProjectBuilder.builder()
                                  .withName('testProject')
                                  .build()
      project.apply plugin: 'groovy'

    when:
      TaskRegistry.INSTANCE.reset()
      project.tasks.create('testTask1', TestTask1)
      project.tasks.create('testTask2', TestTask2)
      TaskRegistry.INSTANCE.resolveDependencies(project)

    then:
      def task1 = project.getTasksByName('testTask1', true).first()
      def dependencies1 = task1.getDependsOn().findAll(TASK_FITER)
      dependencies1[0][0].name == 'testTask2'

      def task2 = project.getTasksByName('testTask2', true).first()
      def dependencies2 = task2.getDependsOn().findAll(TASK_FITER)
      dependencies2[0][0].name == 'check'
  }

  def 'test dependency resolution -- missing dependency'() {
    given:
      def project = ProjectBuilder.builder()
                                  .withName('testProject')
                                  .build()
      project.apply plugin: 'groovy'

    when:
      TaskRegistry.INSTANCE.reset()
      project.tasks.create('testTask1', TestTask1)
      TaskRegistry.INSTANCE.resolveDependencies(project)

    then:
      IllegalStateException exception = thrown()
      exception.message == 'unable to find dependencies of type SHOW_PUBLISH_INFO'
  }
}
