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
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
class ReleaseTask extends DefaultTask {

  ReleaseTask() {
    description = 'Manages release and publishing of artifacts for this project.'
    group = 'Release'
  }

  @TaskAction
  def run() {
    def project = getProject()
    def settings = (ReleaseExtension) project.getExtensions().findByName('release')
    logger.debug('received settings {}', settings)
  }
}
