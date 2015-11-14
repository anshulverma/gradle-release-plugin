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

import net.anshulverma.gradle.release.AbstractSpecificationTest
import net.anshulverma.gradle.release.tasks.fixtures.TestCheckReleaseTask
import net.anshulverma.gradle.release.tasks.fixtures.TestMainTask
import net.anshulverma.gradle.release.tasks.fixtures.TestParentTask
import net.anshulverma.gradle.release.tasks.fixtures.TestPreTask1
import net.anshulverma.gradle.release.tasks.fixtures.TestPreTask2
import net.anshulverma.gradle.release.tasks.fixtures.TestTask1
import net.anshulverma.gradle.release.tasks.fixtures.TestTask2
import org.gradle.api.tasks.TaskInstantiationException

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class TaskRegistryTest extends AbstractSpecificationTest {

  private static final Closure<Boolean> TASK_FITER = { dependency ->
    dependency.class in Collection
  }

  def 'test dependency resolution -- happy path'() {
    given:
      def project = newProject()

    when:
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
      def project = newProject()

    when:
      project.tasks.create('testTask1', TestTask1)
      TaskRegistry.INSTANCE.resolveDependencies(project)

    then:
      IllegalStateException exception = thrown()
      exception.message == 'unable to find tasks named \'showPublishInfo\' in \'testProject\''
  }

  def 'test dependent resolution'() {
    given:
      def project = newProject()

    when:
      TestMainTask mainTask = newTask(TestMainTask, project)
      TaskRegistry.INSTANCE.resolveDependencies(project)
      def checkTask = project.getTasksByName('check', true)[0]

    then:
      checkTask.dependsOn.contains(mainTask)
  }

  def 'test skip with parent not invoked'() {
    given:
      def project = newProject()
      project.gradle.startParameter.taskNames = ['release']

    when:
      def preTask1 = newTask(TestPreTask1, project)
      def preTask2 = newTask(TestPreTask2, project)
      newTask(TestParentTask, project)
      TestCheckReleaseTask checkReleaseTask = newTask(TestCheckReleaseTask, project)
      TaskRegistry.INSTANCE.resolveDependencies(project)

    then:
      checkReleaseTask.dependsOn.contains(preTask1)
      !checkReleaseTask.dependsOn.contains(preTask2)
  }

  def 'test register same task twice'() {
    given:
      def project = newProject()
      project.gradle.startParameter.taskNames = ['release']

    when:
      newTask(TestCheckReleaseTask, project)
      newTask(TestCheckReleaseTask, project)

    then:
      TaskInstantiationException exception = thrown()
      exception.cause.message == 'task of type CHECK_RELEASE has already been registered with context ' +
          'net.anshulverma.gradle.release.tasks.TaskContext(' +
          'task:task \':checkRelease\', type:CHECK_RELEASE, parent:NULL, dependencies:[], dependents:[])'
  }
}
