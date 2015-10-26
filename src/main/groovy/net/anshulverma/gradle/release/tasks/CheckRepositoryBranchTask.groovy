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

import net.anshulverma.gradle.release.annotation.Task
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Task(value = TaskType.CHECK_REPOSITORY_BRANCH,
    description = 'Check current branch is master and it is in sync with remote.')
class CheckRepositoryBranchTask extends AbstractRepositoryTask {

  @Override
  protected execute(Project project) {
    String requiredBranch = 'master'
    getRepository().fetch(project)

    def branch = getRepository().getCurrentBranch(project)
    if (branch != requiredBranch) {
      throw new IllegalStateException(
          "Incorrect release branch: ${branch}. You must be on ${requiredBranch} to release")
    }

    if (!getRepository().isSynced(project)) {
      throw new IllegalStateException('The local branch is not in sync with remote')
    }
  }
}
