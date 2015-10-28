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

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor
import groovy.transform.TypeChecked

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@TupleConstructor
@EqualsAndHashCode
class SemanticVersion {

  int major, minor, patch
  String suffix = ''

  SemanticVersion next(ReleaseType releaseType) {
    if (releaseType == ReleaseType.MAJOR) {
      new SemanticVersion(major + 1, 0, 0, suffix)
    } else if (releaseType == ReleaseType.MINOR) {
      new SemanticVersion(major, minor + 1, 0, suffix)
    } else if (releaseType == ReleaseType.PATCH) {
      new SemanticVersion(major, minor, patch + 1, suffix)
    }
  }

  @Override
  String toString() {
    return "${major}.${minor}.${patch}${suffix ? '-' + suffix : ''}"
  }
}
