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
package net.anshulverma.gradle.release.tasks.fixtures

import groovy.transform.TypeChecked
import groovy.transform.builder.Builder
import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.repository.ProjectRepository
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@Builder
@Slf4j
class TestProjectRepository implements ProjectRepository {

  String currentBranch
  boolean synced
  String status
  String tag

  @Override
  def fetch(Project project) {
    log.info("fetching latest updates for $project.name")
  }

  @Override
  String getCurrentBranch(Project project) {
    currentBranch
  }

  @Override
  boolean isSynced(Project project) {
    synced
  }

  @Override
  String getStatus(Project project) {
    status
  }

  @Override
  String getTag(Project project) {
    tag
  }
}
