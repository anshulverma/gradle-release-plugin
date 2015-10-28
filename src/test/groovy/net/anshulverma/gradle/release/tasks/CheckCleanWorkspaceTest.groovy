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

import net.anshulverma.gradle.release.AbstractRepositorySpecificationTest
import net.anshulverma.gradle.release.info.PropertyName
import net.anshulverma.gradle.release.tasks.fixtures.TestProjectRepository
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class CheckCleanWorkspaceTest extends AbstractRepositorySpecificationTest {

  def 'test clean workspace task with branch status not empty'() {
    given:
      def project = newProject()
      def testRepository = TestProjectRepository.builder()
                                                .status('not empty status')
                                                .build()

    when:
      CheckCleanWorkspaceTask task = newRepositoryTask(CheckCleanWorkspaceTask, testRepository)
      task.execute(project)

    then:
      task.onlyIf.isSatisfiedBy(task)
      IllegalStateException exception = thrown()
      exception.message == 'Workspace is not clean \nnot empty status'
  }

  def 'test check clean workspace task -- happy path'() {
    given:
      def project = newProject()
      def testRepository = TestProjectRepository.builder()
                                                .status('')
                                                .build()

    when:
      CheckCleanWorkspaceTask task = newRepositoryTask(CheckCleanWorkspaceTask, testRepository)
      task.execute(project)

    then:
      task.onlyIf.isSatisfiedBy(task)
      notThrown(IllegalStateException)
  }

  @Unroll
  def 'test skip clean workspace task when property #property is set'() {
    given:
      def project = newProject()
      project.extensions.add(property, 'true')
      def testRepository = TestProjectRepository.builder().build()

    when:
      CheckCleanWorkspaceTask task = newRepositoryTask(CheckCleanWorkspaceTask, testRepository, project)

    then:
      !task.onlyIf.isSatisfiedBy(task)

    where:
      property << [PropertyName.SKIP_ALL_CHECKS.name, PropertyName.SKIP_CLEAN_WORKSPACE_CHECK.name]
  }
}
