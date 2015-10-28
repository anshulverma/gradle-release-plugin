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
package net.anshulverma.gradle.release.version

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.AbstractRepositorySpecificationTest
import net.anshulverma.gradle.release.tasks.fixtures.TestProjectRepository
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class GitTagVersioningStrategyTest extends AbstractRepositorySpecificationTest {

  @Unroll
  def 'test valid version parsing for git tag "#tag"'() {
    given:
      def repository = TestProjectRepository.builder()
                                            .tag(tag)
                                            .build()
      def versioningStrategy = new GitTagVersioningStrategy(repository)

    when:
      SemanticVersion version = versioningStrategy.currentVersion(newProject())

    then:
      expectedVersion == version

    where:
      tag            | expectedVersion
      ''             | new SemanticVersion(0, 0, 0, '')
      '1.2.3'        | new SemanticVersion(1, 2, 3, '')
      '123.456.789'  | new SemanticVersion(123, 456, 789, '')
      '1.23.456-rc1' | new SemanticVersion(1, 23, 456, 'rc1')
  }

  @Unroll
  def 'test invalid version parsing for git tag "#tag"'() {
    given:
      def repository = TestProjectRepository.builder()
                                            .tag(tag)
                                            .build()
      def versioningStrategy = new GitTagVersioningStrategy(repository)

    when:
      versioningStrategy.currentVersion(newProject())

    then:
      IllegalStateException exception = thrown()
      exception.message == "unable to parse semantic version from tag $tag. " +
                           'Please tag your repository with a tag like <major>.<minor>.<patch>-<suffix>'

    where:
      tag << ['1-2.3', 'abcd', 'a.b.c', '1.2.3.4', '1.2a.3', '1.2.3-!', '1.2.3-', '1.2.3--', '.2..3.4']
  }
}
