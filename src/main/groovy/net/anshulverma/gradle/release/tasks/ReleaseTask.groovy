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

import groovy.util.logging.Slf4j
import net.anshulverma.gradle.release.annotation.DependsOn
import net.anshulverma.gradle.release.annotation.Task
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Task(value = TaskType.RELEASE, description = 'Manages release and publishing of artifacts for this project.')
@DependsOn([TaskType.PUBLISH, TaskType.BINTRAY_UPLOAD])
@Slf4j
class ReleaseTask extends AbstractRepositoryTask {

  private static final RELEASE_ID = UUID.randomUUID().toString()

  @Override
  protected execute(Project project) {
    if (getProject().rootProject.hasProperty(RELEASE_ID)) {
      log.info("not tagging repository for project '$project.name' since the repository has already been tagged")
    } else {
      releaseSCM(project.rootProject)
    }
  }

  private releaseSCM(Project project) {
    def version = "v$project.version"
    log.warn "tagging '$project.name' with version '$version'"
    getRepository().addTag(project, version, "Release $version")

    def upstream = getRepository().getUpstream(project)
    log.warn "pushing tag '$version' to remote repository $upstream"
    getRepository().pushTag(project, version)

    project.extensions.add(RELEASE_ID, RELEASE_ID)
  }
}
