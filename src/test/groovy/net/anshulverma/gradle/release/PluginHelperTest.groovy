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

import net.anshulverma.gradle.release.version.ReleaseType
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class PluginHelperTest extends AbstractSpecificationTest {

  @Unroll
  def 'test project version setup for #releaseType and task #taskName'() {
    given:
      def project = newProject()
      project.extensions.add('releaseType', releaseType.name().toLowerCase())
      project.gradle.startParameter.setTaskNames([taskName])

    when:
      new ReleasePluginHelper().setupVersion(project)

    then:
      expectedVersion == project.version

    where:
      releaseType       | taskName   | expectedVersion
      ReleaseType.PATCH | 'snapshot' | '0.0.1-SNAPSHOT'
      ReleaseType.MAJOR | 'release'  | '1.0.0'
      ReleaseType.MAJOR | 'anyTask'  | '1.0.0-SNAPSHOT'
  }

  def 'test for configuring publications'() {
    given:
      def project = newProject()

    when:
      new ReleasePluginHelper().configurePublications(project)

    then:
      project.extensions.extraProperties.has('releaseRepos')
  }
}
