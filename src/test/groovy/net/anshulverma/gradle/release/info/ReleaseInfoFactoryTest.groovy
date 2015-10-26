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
package net.anshulverma.gradle.release.info

import net.anshulverma.gradle.release.AbstractSpecificationTest
import net.anshulverma.gradle.release.version.ReleaseType
import net.anshulverma.gradle.release.version.SemanticVersion
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ReleaseInfoFactoryTest extends AbstractSpecificationTest {

  @Unroll
  def 'test release info factory for #releaseType'() {
    given:
      def project = newProject()
      project.extensions.add('releaseType', releaseType.name().toLowerCase())
      project.gradle.startParameter.setTaskNames([taskName])

    when:
      def actualReleaseInfo = ReleaseInfoFactory.get(project)

    then:
      actualReleaseInfo == releaseInfo

    where:
      releaseType          | taskName  | releaseInfo
      ReleaseType.PATCH    | 'release' | ReleaseInfo.builder()
                                                    .releaseType(ReleaseType.PATCH)
                                                    .isRelease(true)
                                                    .current(new SemanticVersion(0, 0, 0))
                                                    .next(new SemanticVersion(0, 0, 1))
                                                    .author(String.valueOf(System.properties['user.name']))
                                                    .build()
      ReleaseType.MINOR    | 'release' | ReleaseInfo.builder()
                                                    .releaseType(ReleaseType.MINOR)
                                                    .isRelease(true)
                                                    .current(new SemanticVersion(0, 0, 0))
                                                    .next(new SemanticVersion(0, 1, 0))
                                                    .author(String.valueOf(System.properties['user.name']))
                                                    .build()
      ReleaseType.MAJOR    | 'release' | ReleaseInfo.builder()
                                                    .releaseType(ReleaseType.MAJOR)
                                                    .isRelease(true)
                                                    .current(new SemanticVersion(0, 0, 0))
                                                    .next(new SemanticVersion(1, 0, 0))
                                                    .author(String.valueOf(System.properties['user.name']))
                                                    .build()
      ReleaseType.SNAPSHOT | ''        | ReleaseInfo.builder()
                                                    .releaseType(ReleaseType.SNAPSHOT)
                                                    .isRelease(false)
                                                    .current(new SemanticVersion(0, 0, 0))
                                                    .next(new SemanticVersion(0, 0, 0, 'SNAPSHOT'))
                                                    .author(String.valueOf(System.properties['user.name']))
                                                    .build()
  }
}
