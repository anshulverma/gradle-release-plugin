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

import net.anshulverma.gradle.release.info.PropertyName
import net.anshulverma.gradle.release.repository.ProjectRepositoryWrapper
import net.anshulverma.gradle.release.tasks.fixtures.TestProjectRepository
import net.anshulverma.gradle.release.version.UserDefinedVersioningStrategy
import net.anshulverma.gradle.release.version.VersioningStrategyFactory

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ReleaseExtensionTest extends AbstractSpecificationTest {

  def 'allow user to define versioning strategy'() {
    def project = newProject()
    def testRepository = TestProjectRepository.builder()
                                              .tag('1.2.3.4')
                                              .build()
    def wrappedRepository = new ProjectRepositoryWrapper(project, testRepository)

    given:
      def closure = {
        versioning { repository ->
          String tag = repository.tag
          tag.split('\\.')
        }
      }
      project.extensions.add(PropertyName.RELEASE_SETTINGS.name, closure)

    when:
      def extension = ReleaseExtension.getSettings(project)

    then:
      extension.hasVersioning()
      extension.currentVersionClosure != null

    when:
      def (major, minor, patch, suffix) = extension.currentVersionClosure(wrappedRepository)

    then:
      major == '1'
      minor == '2'
      patch == '3'
      suffix == '4'
  }

  def 'check all delegate methods are called for user defined strategy'() {
    def project = newProject()
    def testRepository = TestProjectRepository.builder()
                                              .currentBranch('test-master')
                                              .synced(false)
                                              .status('dummy status')
                                              .tag('1.2.3.4')
                                              .upstream('test upstream')
                                              .build()
    def wrappedRepository = new ProjectRepositoryWrapper(project, testRepository)

    given:
      def closure = {
        versioning { repository ->
          assert repository.currentBranch == 'test-master'
          assert !repository.synced
          assert repository.status == 'dummy status'
          assert repository.tag == '1.2.3.4'
          assert repository.upstream == 'test upstream'

          [1, 2, 3, 'alpha']
        }
      }
      project.extensions.add(PropertyName.RELEASE_SETTINGS.name, closure)

    when:
      def extension = ReleaseExtension.getSettings(project)
      def (major, minor, patch, suffix) = extension.currentVersionClosure(wrappedRepository)

    then:
      major == 1
      minor == 2
      patch == 3
      suffix == 'alpha'
  }

  def 'versioning factory uses release property'() {
    def project = newProject()
    def testRepository = TestProjectRepository.builder().build()

    given:
      def closure = {
        versioning { repository ->
          [1, 2, 3, 'alpha']
        }
      }
      project.extensions.add(PropertyName.RELEASE_SETTINGS.name, closure)

    when:
      def versioningStrategy = VersioningStrategyFactory.get(project, testRepository)

    then:
      versioningStrategy instanceof UserDefinedVersioningStrategy
  }
}
