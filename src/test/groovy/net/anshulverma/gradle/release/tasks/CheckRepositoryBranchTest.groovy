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

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.AbstractRepositorySpecificationTest
import spock.lang.Unroll

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Slf4j
class CheckRepositoryBranchTest extends AbstractRepositorySpecificationTest {

  @Unroll
  def 'test check repository branch task with branch #branch and isSynced #isSynced'() {
    given:
      def project = newProject()
      def testRepository = new TestProjectRepository(branch, isSynced, null)

    when:
      CheckRepositoryBranchTask task = newRepositoryTask(CheckRepositoryBranchTask, testRepository)
      task.execute(project)

    then:
      IllegalStateException exception = thrown()
      exception.message == message

    where:
      branch   | isSynced | message
      'dev'    | true     | 'Incorrect release branch: dev. You must be on master to release'
      'master' | false    | 'The local branch is not in sync with remote'
  }

  def 'test check repository branch task -- happy path'() {
    given:
      def project = newProject()
      def testRepository = new TestProjectRepository('master', true, null)

    when:
      CheckRepositoryBranchTask task = newRepositoryTask(CheckRepositoryBranchTask, testRepository)
      task.execute(project)

    then:
      notThrown(IllegalStateException)
  }
}