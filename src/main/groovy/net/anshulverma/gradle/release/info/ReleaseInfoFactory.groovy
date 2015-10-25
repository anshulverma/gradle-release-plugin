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
import net.anshulverma.gradle.release.version.ReleaseType
import net.anshulverma.gradle.release.version.VersioningStrategy
import net.anshulverma.gradle.release.version.VersioningStrategyFactory
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
class ReleaseInfoFactory {

  private final static ReleaseType DEFAULT_RELEASE_TYPE = ReleaseType.PATCH

  private ReleaseInfoFactory() { }

  static ReleaseInfo get(Project project) {
    VersioningStrategy versioningStrategy = VersioningStrategyFactory.get(project)
    ReleaseType releaseType = getReleaseType(project)
    return ReleaseInfo.builder()
                      .releaseType(releaseType)
                      .isRelease(project.gradle.startParameter.taskNames.contains('release'))
                      .current(versioningStrategy.currentVersion(project))
                      .next(versioningStrategy.nextVersion(project, releaseType))
                      .author(String.valueOf(System.properties['user.name']))
                      .build()
  }

  private static ReleaseType getReleaseType(Project project) {
    if (project.hasProperty('releaseType')) {
      return ReleaseType.fromName(String.valueOf(project.property('releaseType')), DEFAULT_RELEASE_TYPE)
    }
    DEFAULT_RELEASE_TYPE
  }
}
