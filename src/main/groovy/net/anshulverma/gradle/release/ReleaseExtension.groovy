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
package net.anshulverma.gradle.release

import groovy.transform.TypeChecked
import net.anshulverma.gradle.release.info.PropertyName
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
class ReleaseExtension {

  final Project project

  ReleaseExtension(Project project) {
    this.project = project
  }

  Closure currentVersionClosure

  static ReleaseExtension getSettings(Project project) {
    def settings = new ReleaseExtension(project)
    if (project.hasProperty(PropertyName.RELEASE_SETTINGS.name)) {
      Closure closure = (Closure) project.property(PropertyName.RELEASE_SETTINGS.name)
      closure.delegate = settings
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure()
    }
    settings
  }

  def versioning(Closure currentVersionClosure) {
    this.currentVersionClosure = currentVersionClosure
  }

  def hasVersioning() {
    currentVersionClosure != null
  }
}
