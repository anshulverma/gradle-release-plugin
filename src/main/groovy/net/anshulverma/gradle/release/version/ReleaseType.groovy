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

import groovy.transform.TypeChecked

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
enum ReleaseType {
  MAJOR( { SemanticVersion currentVersion ->
    new SemanticVersion(currentVersion.major + 1,
                        currentVersion.minor,
                        currentVersion.patch,
                        currentVersion.suffix)
  }),
  MINOR( { SemanticVersion currentVersion ->
    new SemanticVersion(currentVersion.major,
                        currentVersion.minor + 1,
                        currentVersion.patch,
                        currentVersion.suffix)
  }),
  PATCH( { SemanticVersion currentVersion ->
    new SemanticVersion(currentVersion.major,
                        currentVersion.minor,
                        currentVersion.patch + 1,
                        currentVersion.suffix)
  }),
  SNAPSHOT( { SemanticVersion currentVersion ->
    new SemanticVersion(currentVersion.major,
                        currentVersion.minor,
                        currentVersion.patch,
                        'SNAPSHOT')
  })

  final Closure<SemanticVersion> upgrader

  ReleaseType(Closure upgrader) {
    this.upgrader = upgrader
  }

  SemanticVersion upgrade(SemanticVersion currentVersion) {
    upgrader(currentVersion)
  }

  static ReleaseType fromName(String name, ReleaseType defaultReleaseType) {
    if (!name) {
      return defaultReleaseType
    }
    valueOf(name.toUpperCase())
  }
}
