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
import net.anshulverma.gradle.release.repository.ProjectRepository

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ReleaseTaskTest extends AbstractRepositorySpecificationTest {

  def 'test release task actions'() {
    given:
      def project = newProject()
      project.version = '1.2.3'

      def testRepository = Mock(ProjectRepository)
      testRepository.getUpstream(project) >> 'test-upstream'

    when:
      ReleaseTask task = newRepositoryTask(ReleaseTask, testRepository, project)
      task.execute(project)

    then:
      1 * testRepository.addTag(project, 'v1.2.3', 'Release v1.2.3')
      1 * testRepository.pushTag(project, 'v1.2.3')
  }

  def 'test multimodule release task'() {
    given:
      def project = newMultiModuleProject()
      project.version = '1.2.3'

      def testRepository = Mock(ProjectRepository)
      testRepository.getUpstream(project) >> 'test-upstream'

    when:
      project.allprojects {
        ReleaseTask task = newRepositoryTask(ReleaseTask, testRepository, it)
        task.execute(it)
      }

    then:
      1 * testRepository.addTag(project, 'v1.2.3', 'Release v1.2.3')
      1 * testRepository.pushTag(project, 'v1.2.3')
  }
}
