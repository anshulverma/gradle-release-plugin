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
import net.anshulverma.gradle.release.repository.ProjectRepositoryProvider
import net.anshulverma.gradle.release.tasks.fixtures.TestProjectRepository
import net.anshulverma.gradle.release.version.ReleaseType
import net.anshulverma.gradle.release.version.SemanticVersion
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ReleaseInfoFactoryTest extends AbstractSpecificationTest {

  private static final AUTHOR = String.valueOf(System.properties['user.name'])

  @SuppressWarnings(['BracesForMethod', 'MethodSize'])
  @Unroll
  def '''should be able to build release info for task #taskName
         when tag is "#tag",
         number of commits since tag is #commits and
         releaseType is #releaseType'''() {
    given:
      def project = newProject()
      project.extensions.add('releaseType', releaseType.name().toLowerCase())
      project.gradle.startParameter.setTaskNames([taskName])

      def testProjectRepository = TestProjectRepository.builder()
                                                       .tag(tag)
                                                       .commitsSinceLastTag(commits)
                                                       .status('')
                                                       .build()
      ProjectRepositoryProvider.instance.projectRepository = testProjectRepository

    when:
      def actualReleaseInfo = ReleaseInfoFactory.INSTANCE.getOrCreate(project, testProjectRepository)

    then:
      actualReleaseInfo == releaseInfo

    where:
      releaseType       | taskName   | tag     | commits | releaseInfo
      ReleaseType.PATCH | 'release'  | ''      | 10      | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.PATCH)
                                                                      .isRelease(true)
                                                                      .current(new SemanticVersion(0, 0, 0))
                                                                      .next(new SemanticVersion(0, 0, 1))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.PATCH | 'release'  | ''      | 0       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.PATCH)
                                                                      .isRelease(true)
                                                                      .current(new SemanticVersion(0, 0, 0))
                                                                      .next(new SemanticVersion(0, 0, 1))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.PATCH | 'release'  | '1.2.3' | 10      | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.PATCH)
                                                                      .isRelease(true)
                                                                      .current(new SemanticVersion(1, 2, 3))
                                                                      .next(new SemanticVersion(1, 2, 4))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.PATCH | 'release'  | '1.2.3' | 0       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.PATCH)
                                                                      .isRelease(true)
                                                                      .current(new SemanticVersion(1, 2, 3))
                                                                      .next(new SemanticVersion(1, 2, 4))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MINOR | 'snapshot' | ''      | 5       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MINOR)
                                                                      .isRelease(false)
                                                                      .current(new SemanticVersion(0, 0, 0))
                                                                      .next(new SemanticVersion(0, 1, 0, 'SNAPSHOT'))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MINOR | 'snapshot' | ''      | 0       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MINOR)
                                                                      .isRelease(false)
                                                                      .current(new SemanticVersion(0, 0, 0))
                                                                      .next(new SemanticVersion(0, 1, 0, 'SNAPSHOT'))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MINOR | 'snapshot' | '1.2.3' | 5       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MINOR)
                                                                      .isRelease(false)
                                                                      .current(new SemanticVersion(1, 2, 3))
                                                                      .next(new SemanticVersion(1, 3, 0, 'SNAPSHOT'))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MINOR | 'snapshot' | '1.2.3' | 0       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MINOR)
                                                                      .isRelease(false)
                                                                      .current(new SemanticVersion(1, 2, 3))
                                                                      .next(new SemanticVersion(1, 3, 0, 'SNAPSHOT'))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MAJOR | 'anytask'  | ''      | 3       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MAJOR)
                                                                      .isRelease(false)
                                                                      .current(new SemanticVersion(0, 0, 0))
                                                                      .next(new SemanticVersion(1, 0, 0, 'SNAPSHOT'))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MAJOR | 'anytask'  | ''      | 0       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MAJOR)
                                                                      .isRelease(true)
                                                                      .current(new SemanticVersion(0, 0, 0))
                                                                      .next(new SemanticVersion(1, 0, 0))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MAJOR | 'anytask'  | '1.2.3' | 3       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MAJOR)
                                                                      .isRelease(false)
                                                                      .current(new SemanticVersion(1, 2, 3))
                                                                      .next(new SemanticVersion(2, 0, 0, 'SNAPSHOT'))
                                                                      .author(AUTHOR)
                                                                      .build()
      ReleaseType.MAJOR | 'anytask'  | '1.2.3' | 0       | ReleaseInfo.builder()
                                                                      .releaseType(ReleaseType.MAJOR)
                                                                      .isRelease(true)
                                                                      .current(new SemanticVersion(1, 2, 3))
                                                                      .next(new SemanticVersion(1, 2, 3))
                                                                      .author(AUTHOR)
                                                                      .build()
  }
}
