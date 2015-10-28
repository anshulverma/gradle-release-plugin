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

import net.anshulverma.gradle.release.repository.ProjectRepository
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class GitTagVersioningStrategy implements VersioningStrategy {

  final ProjectRepository repository

  GitTagVersioningStrategy(ProjectRepository repository) {
    this.repository = repository
  }

  @Override
  SemanticVersion currentVersion(Project project) {
    def (major, minor, patch, suffix) = parseTag(project)
    return new SemanticVersion(major, minor, patch, suffix)
  }

  private def parseTag(Project project) {
    def tag = repository.getTag(project)
    if (!tag) {
      return [0, 0, 0, '']
    }
    def parsed = TagParser.parse(tag)
    [parsed.major, parsed.minor, parsed.patch, parsed.suffix ? parsed.suffix : '']
  }

  @Override
  SemanticVersion nextVersion(SemanticVersion currentVersion, ReleaseType releaseType) {
    releaseType.upgrade(currentVersion)
  }
}
