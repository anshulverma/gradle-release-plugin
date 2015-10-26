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
package net.anshulverma.gradle.release.repository

import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class GitProjectRepository implements ProjectRepository {

  @Override
  def fetch(Project project) {
    exec(project, 'git', 'fetch')
  }

  @Override
  String getCurrentBranch(Project project) {
    exec(project, 'git', 'rev-parse', '--abbrev-ref', 'HEAD')
  }

  @Override
  boolean isSynced(Project project) {
    return !exec(project, 'git', 'status', '-sb').contains('[')
  }

  @Override
  String getStatus(Project project) {
    return exec(project, 'git', 'status', '--porcelain')
  }

  private String exec(Project project, String... commandArgs) {
    def outputStream = new ByteArrayOutputStream()
    project.exec {
      commandLine commandArgs
      standardOutput = outputStream
    }
    outputStream.toString().trim()
  }
}
