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
import net.anshulverma.gradle.release.repository.ProjectRepository
import net.anshulverma.gradle.release.version.ReleaseType
import net.anshulverma.gradle.release.version.SemanticVersion
import net.anshulverma.gradle.release.version.VersioningStrategy
import net.anshulverma.gradle.release.version.VersioningStrategyFactory
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
class ReleaseInfoFactory {

  private final static ReleaseType DEFAULT_RELEASE_TYPE = ReleaseType.PATCH
  private static final String SNAPSHOT_SUFFIX = 'SNAPSHOT'

  private ReleaseInfoFactory() { }

  static ReleaseInfo get(Project project) {
    get(project, VersioningStrategyFactory.get(project))
  }

  static ReleaseInfo get(Project project, ProjectRepository repository) {
    get(project, VersioningStrategyFactory.get(project, repository))
  }

  static ReleaseInfo get(Project project, VersioningStrategy versioningStrategy) {
    def isRelease = project.gradle.startParameter.taskNames.contains('release')
    ReleaseType releaseType = getReleaseType(project)
    def currentVersion = versioningStrategy.currentVersion(project)
    def nextVersion = getNextVersion(versioningStrategy, currentVersion, releaseType, isRelease)
    return ReleaseInfo.builder()
                      .releaseType(releaseType)
                      .isRelease(isRelease)
                      .current(currentVersion)
                      .next(nextVersion)
                      .author(String.valueOf(System.properties['user.name']))
                      .build()
  }

  private static SemanticVersion getNextVersion(VersioningStrategy versioningStrategy,
                                                SemanticVersion currentVersion,
                                                ReleaseType releaseType,
                                                boolean isRelease) {
    def version = versioningStrategy.nextVersion(currentVersion, releaseType)
    if (!isRelease) {
      version.suffix = SNAPSHOT_SUFFIX
    }
    version
  }

  private static ReleaseType getReleaseType(Project project) {
    if (project.hasProperty('releaseType')) {
      return ReleaseType.fromName(String.valueOf(project.property('releaseType')), DEFAULT_RELEASE_TYPE)
    }
    DEFAULT_RELEASE_TYPE
  }
}
