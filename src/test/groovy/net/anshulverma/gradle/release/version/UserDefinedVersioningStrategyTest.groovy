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
class UserDefinedVersioningStrategyTest extends AbstractRepositorySpecificationTest {

  @Unroll
  def 'test valid user defined version parsing for tag "#tag"'() {
    given:
      def repository = TestProjectRepository.builder()
                                            .tag(tag)
                                            .build()
      def closure = {
        tag.split(/\./)
      }
      def versioningStrategy = new UserDefinedVersioningStrategy(repository, closure)

    when:
      SemanticVersion version = versioningStrategy.currentVersion(newProject())

    then:
      expectedVersion == version

    where:
      tag            | expectedVersion
      '1.2.3.alpha'  | new SemanticVersion(1, 2, 3, 'alpha')
      '1.23.456.rc1' | new SemanticVersion(1, 23, 456, 'rc1')
  }

  def 'test invalid version parsing for user defined versioning strategy'() {
    given:
      def repository = TestProjectRepository.builder().build()
      def closure = {
        ['a', 'b', 'c', 'beta']
      }
      def versioningStrategy = new UserDefinedVersioningStrategy(repository, closure)

    when:
      versioningStrategy.currentVersion(newProject())

    then:
      IllegalStateException exception = thrown()
      exception.message == 'Invalid version format returned by versioning function. ' +
          'Your user defined version function should return an array of size 4 which should have ' +
          'numerical values in the first three positions.'
  }
}
