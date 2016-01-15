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
class VersionTemplateLine implements Comparable<VersionTemplateLine> {

  int lineNumber
  String template

  VersionTemplateLine(lineNumber, template) {
    this.lineNumber = lineNumber
    this.template = template
  }

  @Override
  int compareTo(VersionTemplateLine other) {
    lineNumber - other.lineNumber
  }
}
