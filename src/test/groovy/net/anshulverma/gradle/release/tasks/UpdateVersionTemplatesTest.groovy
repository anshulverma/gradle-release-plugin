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
import org.gradle.api.GradleException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class UpdateVersionTemplatesTest extends AbstractRepositorySpecificationTest {

  def 'test versions are properly updated in template files'() {
    given:
      def project = newProject()
      TestProjectRepository testRepository = TestProjectRepository.builder()
                                                                  .tag('3.2.4')
                                                                  .status('this will be changed')
                                                                  .build()

      def closure = {
        versionedFiles << [
            'template_1': [
                1: 'the version number is $currentVersion',
                4: 'replaced with $nextVersion'
            ],
            'template_2': [
                2: '"$releaseType" "$isRelease" "$currentVersion" "$nextVersion.suffix" "$releaseType"'
            ]
        ]
      }

      def testFile1 = "$project.rootDir/template_1"
      def testFileTemplate1 = "${testFile1}.release-template"
      new File(testFileTemplate1).withWriter { out ->
        out.println 'the version number is bla'
        out.println 'noop'
        out.println 'is this release?: <% print isRelease ? "no" : "yes" %>'
        out.println 'foo-bar'
      }

      def testFile2 = "$project.rootDir/template_2"
      def testFileTemplate2 = "$project.rootDir/template_2.release-template"
      new File(testFile2).withWriter { out ->
        out.println 'lorem ipsum'
        out.println ''
        out.println 'last line'
      }

      project.extensions.add(PropertyName.RELEASE_SETTINGS.name, closure)

    when:
      UpdateVersionTemplatesTask task = newRepositoryTask(UpdateVersionTemplatesTask, testRepository)
      task.execute(project)

    then:
      Files.exists(Paths.get(testFile1))
      Files.exists(Paths.get(testFileTemplate1))
      new File(testFileTemplate1).text == '''the version number is bla
noop
is this release?: <% print isRelease ? "no" : "yes" %>
foo-bar
'''
      new File(testFile1).text == '''the version number is 3.2.4
noop
is this release?: yes
replaced with 3.2.5-SNAPSHOT
'''

      Files.exists(Paths.get(testFile2))
      Files.notExists(Paths.get(testFileTemplate2))
      new File(testFile2).text == '''lorem ipsum
"PATCH" "false" "3.2.4" "SNAPSHOT" "PATCH"
last line
'''

      testRepository.commitMessage == 'updated versions info in 2 files for release v3.2.5-SNAPSHOT'
      testRepository.isPushed
  }

  def 'test file executable permissions are maintened'() {
    given:
      def project = newProject()
      TestProjectRepository testRepository = TestProjectRepository.builder()
                                                                  .tag('3.2.4')
                                                                  .status('this will be changed')
                                                                  .build()

      def closure = {
        versionedFiles << [
            'version-template': [
                1: 'the version number is $currentVersion',
            ]
        ]
      }

      def testFilePath = "$project.rootDir/version-template"
      def testFileTemplate = new File("${testFilePath}.release-template")
      testFileTemplate.withWriter { out ->
        out.println 'the version number is bla'
      }
      testFileTemplate.executable = true

      project.extensions.add(PropertyName.RELEASE_SETTINGS.name, closure)

    when:
      UpdateVersionTemplatesTask task = newRepositoryTask(UpdateVersionTemplatesTask, testRepository)
      task.execute(project)

    then:
      Files.exists(Paths.get(testFilePath))

      def testFile = new File(testFilePath)
      testFile.text == '''the version number is 3.2.4
'''
      testFile.canExecute()

      testRepository.commitMessage == 'updated versions info in 1 files for release v3.2.5-SNAPSHOT'
      testRepository.isPushed
  }

  def 'test version update task when input file is missing'() {
    given:
      def project = newProject()

      def closure = {
        versionedFiles << [
            'template_1': [
                1: 'the version number is $currentVersion',
            ]
        ]
      }

      project.extensions.add(PropertyName.RELEASE_SETTINGS.name, closure)

    when:
      UpdateVersionTemplatesTask task = newTask(UpdateVersionTemplatesTask)
      task.execute(project)

    then:
      GradleException exception = thrown()
      exception.message == "unable to find input template file - ${project.rootDir}/template_1"
  }

  def 'test nothing happens if versionedFiles is not defined'() {
    given:
      def project = newProject()

    when:
      UpdateVersionTemplatesTask task = newTask(UpdateVersionTemplatesTask)
      task.execute(project)

    then:
      notThrown(Exception)
  }
}
