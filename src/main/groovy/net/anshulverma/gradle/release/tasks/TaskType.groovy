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

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
enum TaskType {
  NULL('null'),

  /* custom tasks */
  RELEASE('release'),
  PRE_RELEASE('preRelease'),
  SNAPSHOT('snapshot'),
  PRE_SNAPSHOT('preSnapshot'),
  PREPARE_RELEASE('prepareRelease'),
  CHECK_RELEASE('checkRelease'),
  SHOW_PUBLISH_INFO('showPublishInfo'),
  CHECK_CLEAN_WORKSPACE('checkCleanWorkspace'),
  CHECK_REPOSITORY_BRANCH('checkRepositoryBranch'),
  VERSION_PROJECT('versionProject'),
  UPDATE_VERSION_TEMPLATES('updateVersionTemplates'),

  /* tasks created outside of this plugin that some custom tasks depend on */
  PUBLISH('publish'),
  BINTRAY_UPLOAD('bintrayUpload'),
  CHECK('check')

  String taskName

  private TaskType(String taskName) {
    this.taskName = taskName
  }
}
