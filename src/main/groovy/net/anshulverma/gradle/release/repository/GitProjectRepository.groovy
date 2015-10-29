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

import net.anshulverma.gradle.release.common.Logger
import org.gradle.api.Project
import org.gradle.process.internal.ExecException

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
    !exec(project, 'git', 'status', '-sb').contains('[')
  }

  @Override
  String getStatus(Project project) {
    exec(project, 'git', 'status', '--porcelain')
  }

  @Override
  String getTag(Project project) {
    try {
      return exec(project, 'git', 'describe', '--abbrev=0', '--tags')
    } catch (ExecException ignored) {
      Logger.warn(project,
                  'WARNING: project repository does not have a tag. ' +
                      'Please refer to this page for preferred tagging practices for release plugin: ' +
                      'https://github.com/anshulverma/gradle-release-plugin')
      return ''
    }
  }

  private String exec(Project project, String... commandArgs) {
    def outputStream = new ByteArrayOutputStream()
    project.exec {
      commandLine commandArgs
      standardOutput = outputStream
      errorOutput = outputStream
    }
    outputStream.toString().trim()
  }
}
