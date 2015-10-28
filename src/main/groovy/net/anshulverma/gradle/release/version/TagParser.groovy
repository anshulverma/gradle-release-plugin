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

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class TagParser {

  private static final TAG_REGEX = /^([0-9]+)\.([0-9]+)\.([0-9]+)(-([0-9a-zA-Z]+))?$/

  static def parse(String tag) {
    def matcher = (tag =~ TAG_REGEX)
    if (!matcher.find()) {
      throw new IllegalStateException("unable to parse semantic version from tag $tag. " +
                                      'Please tag your repository with a tag like <major>.<minor>.<patch>-<suffix>')
    }
    [
        major : matcher[0][1] as Integer,
        minor : matcher[0][2] as Integer,
        patch : matcher[0][3] as Integer,
        suffix: matcher[0][5]
    ]
  }
}
