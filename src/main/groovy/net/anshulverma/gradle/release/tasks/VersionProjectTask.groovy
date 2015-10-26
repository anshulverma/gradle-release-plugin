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
package net.anshulverma.gradle.release.tasks

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.annotation.Task
import net.anshulverma.gradle.release.info.ReleaseInfo
import net.anshulverma.gradle.release.info.ReleaseInfoFactory
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@Task(value = TaskType.VERSION_PROJECT,
      description = 'Setup version for this project.')
@Slf4j
class VersionProjectTask extends AbstractReleaseTask {

  @Override
  protected execute(Project project) {
    ReleaseInfo releaseInfo = ReleaseInfoFactory.get(project)
    project.version = releaseInfo.next.toString()
    log.warn "setting version for $project.name to $releaseInfo.next "
  }
}
