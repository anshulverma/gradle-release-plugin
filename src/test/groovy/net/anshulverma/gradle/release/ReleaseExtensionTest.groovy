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
import net.anshulverma.gradle.release.info.ReleaseInfo
import net.anshulverma.gradle.release.info.ReleaseInfoTemplateEvaluator
import net.anshulverma.gradle.release.repository.ProjectRepositoryWrapper
import net.anshulverma.gradle.release.tasks.fixtures.TestProjectRepository
import net.anshulverma.gradle.release.version.ReleaseType
import net.anshulverma.gradle.release.version.SemanticVersion
import net.anshulverma.gradle.release.version.UserDefinedVersioningStrategy
import net.anshulverma.gradle.release.version.template.VersionTemplateConfigCollection
import net.anshulverma.gradle.release.version.VersioningStrategyFactory

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ReleaseExtensionTest extends AbstractSpecificationTest {

  def 'allow user to define versioning strategy'() {
    given:
      def project = newProject()
      def testRepository = TestProjectRepository.builder()
                                                .tag('1.2.3.4')
                                                .build()
      def wrappedRepository = new ProjectRepositoryWrapper(project, testRepository)

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
    given:
      def project = newProject()
      def testRepository = TestProjectRepository.builder()
                                                .currentBranch('test-master')
                                                .synced(false)
                                                .status('dummy status')
                                                .tag('1.2.3.4')
                                                .upstream('test upstream')
                                                .build()
      def wrappedRepository = new ProjectRepositoryWrapper(project, testRepository)

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
    given:
      def project = newProject()
      def testRepository = TestProjectRepository.builder().build()

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

  def 'provide input files with line number and string template'() {
    given:
      def project = newProject()

      def closure = {
        versionedFiles << [
            ("$project.rootDir/test_file_1"): [
                29: 'the version number is $currentVersion',
                10: 'no version info here'
            ],
            'test_file_2': [
                26: '"$releaseType" "$isRelease" "$currentVersion" "$nextVersion.suffix" "$releaseType" "$author" ' +
                    '"$currentVersionWithSuffix"'
            ],
            'test_file_3.release-template': [
                30: 'this is<% print isRelease ? "" : " not" %> a release version'
            ]
        ]
      }

      new File("$project.rootDir/test_file_2.release-template") << 'first line\n'

      project.extensions.add(PropertyName.RELEASE_SETTINGS.name, closure)
      def releaseInfo = ReleaseInfo.builder()
                                   .releaseType(ReleaseType.MINOR)
                                   .isRelease(false)
                                   .current(new SemanticVersion(1, 2, 3, 'abcd'))
                                   .next(new SemanticVersion(2, 3, 4, 'xyz'))
                                   .author('test author')
                                   .build()
      def evaluator = new ReleaseInfoTemplateEvaluator(project, releaseInfo)

    when:
      def templatesConfigIterator = VersionTemplateConfigCollection.get(project).iterator()
      def templateConfig = templatesConfigIterator.next()

    then:
      templateConfig.inputFile.toString() == "$project.rootDir/test_file_1"
      templateConfig.outputFile.toString() == "$project.rootDir/test_file_1"
      templateConfig.lines.size() == 2
      // lines must be re-sorted
      evaluator.evaluate(templateConfig.lines[0].template) == 'no version info here'
      evaluator.evaluate(templateConfig.lines[1].template) == 'the version number is 1.2.3-abcd'

    when:
      templateConfig = templatesConfigIterator.next()

    then:
      templateConfig.inputFile.toString() == "$project.rootDir/test_file_2.release-template"
      templateConfig.outputFile.toString() == "$project.rootDir/test_file_2"
      templateConfig.lines.size() == 1
      evaluator.evaluate(templateConfig.lines[0].template) ==
          '"MINOR" "false" "1.2.3-abcd" "xyz" "MINOR" "test author" "1.2.3-abcd-SNAPSHOT"'

    when:
      templateConfig = templatesConfigIterator.next()

    then:
      templateConfig.inputFile.toString() == "$project.rootDir/test_file_3.release-template"
      templateConfig.outputFile.toString() == "$project.rootDir/test_file_3"
      templateConfig.lines.size() == 1
      evaluator.evaluate(templateConfig.lines[0].template) == 'this is not a release version'

    !templatesConfigIterator.hasNext()
  }
}
