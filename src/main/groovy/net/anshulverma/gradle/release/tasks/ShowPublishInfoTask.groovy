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
import net.anshulverma.gradle.release.annotation.Task
import net.anshulverma.gradle.release.info.ReleaseInfo
import net.anshulverma.gradle.release.info.ReleaseInfoFactory
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
@Task(value = TaskType.SHOW_PUBLISH_INFO, description = 'Displays information that will be used to publish this project.')
class ShowPublishInfoTask extends AbstractTask {

  @Override
  protected execute(Project project) {
    ReleaseInfo releaseInfo = ReleaseInfoFactory.get(project)
    println """════════════════════════════════════════
Current version : $releaseInfo.current
   Next version : $releaseInfo.next
   Release type : $releaseInfo.releaseType
    Is release? : ${releaseInfo.isRelease ? 'yes' : 'no'}
         Author : $releaseInfo.author
═════════════════════════════════════════"""
  }
}
