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

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.repository.ProjectRepository
import net.anshulverma.gradle.release.repository.ProjectRepositoryProvider
import net.anshulverma.gradle.release.tasks.TaskType
import net.anshulverma.gradle.release.version.ReleaseType
import net.anshulverma.gradle.release.version.SemanticVersion
import net.anshulverma.gradle.release.version.VersioningStrategy
import net.anshulverma.gradle.release.version.VersioningStrategyFactory
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@Slf4j
class ReleaseInfoFactory {

  static final ReleaseInfoFactory INSTANCE = new ReleaseInfoFactory()
  static final ReleaseType DEFAULT_RELEASE_TYPE = ReleaseType.PATCH

  private static final String SNAPSHOT_SUFFIX = 'SNAPSHOT'

  private final Map<String, ReleaseInfo> releaseInfoMap = [:]

  private ReleaseInfoFactory() { }

  /**
   * Needed for unit testing
   */
  def reset() {
    releaseInfoMap.clear()
    log.info('project release info cleared')
  }

  ReleaseInfo getOrCreate(Project project) {
    getOrCreate(project, VersioningStrategyFactory.get(project, ProjectRepositoryProvider.instance.projectRepository))
  }

  ReleaseInfo getOrCreate(Project project, ProjectRepository repository) {
    getOrCreate(project, VersioningStrategyFactory.get(project, repository))
  }

  ReleaseInfo getOrCreate(Project project, VersioningStrategy versioningStrategy) {
    if (!releaseInfoMap.containsKey(project.name)) {
      releaseInfoMap[project.name] = create(project, versioningStrategy)
    }
    releaseInfoMap[project.name]
  }

  private ReleaseInfo create(Project project, VersioningStrategy versioningStrategy) {
    def isRelease = getIsRelease(project)
    ReleaseType releaseType = getReleaseType(project)
    def currentVersion = versioningStrategy.currentVersion(project)
    def nextVersion = getNextVersion(versioningStrategy, currentVersion, releaseType, isRelease)
    ReleaseInfo.builder()
               .releaseType(releaseType)
               .isRelease(isRelease)
               .current(currentVersion)
               .next(nextVersion)
               .author(String.valueOf(System.properties['user.name']))
               .build()
  }

  private boolean getIsRelease(Project project) {
    def taskNames = project.gradle.startParameter.taskNames

    def repository = ProjectRepositoryProvider.instance.projectRepository
    taskNames.contains(TaskType.RELEASE.taskName) ||
        (!taskNames.contains(TaskType.SNAPSHOT.taskName) &&
            repository.getCommitCountSinceTag(project) == 0 && repository.getStatus(project).empty)
  }

  private SemanticVersion getNextVersion(VersioningStrategy versioningStrategy,
                                         SemanticVersion currentVersion,
                                         ReleaseType releaseType,
                                         boolean isRelease) {
    def version = versioningStrategy.nextVersion(currentVersion, releaseType)
    if (!isRelease) {
      version.suffix = SNAPSHOT_SUFFIX
    }
    version
  }

  private ReleaseType getReleaseType(Project project) {
    String releaseType = new ProjectPropertyReader(project).getStringProperty(PropertyName.RELEASE_TYPE)
    ReleaseType.fromName(project, releaseType, DEFAULT_RELEASE_TYPE)
  }
}
