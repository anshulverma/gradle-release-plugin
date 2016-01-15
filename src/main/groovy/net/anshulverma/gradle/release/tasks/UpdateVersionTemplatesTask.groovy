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

import net.anshulverma.gradle.release.annotation.DependsOn
import net.anshulverma.gradle.release.annotation.Task
import net.anshulverma.gradle.release.common.Logger
import net.anshulverma.gradle.release.info.ReleaseInfoFactory
import net.anshulverma.gradle.release.info.ReleaseInfoTemplateEvaluator
import net.anshulverma.gradle.release.version.VersionTemplatesConfig
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@Task(value = TaskType.UPDATE_VERSION_TEMPLATES, description = 'Update templates by inserting new version numbers.')
@DependsOn([
    TaskType.CHECK_RELEASE
])
class UpdateVersionTemplatesTask extends AbstractRepositoryTask {

  @Override
  protected execute(Project project) {
    def config = VersionTemplatesConfig.get(project)
    if (config.templateFiles.isEmpty()) {
      return
    }

    def releaseInfo = ReleaseInfoFactory.INSTANCE.getOrCreate(project, getRepository())
    def evaluator = new ReleaseInfoTemplateEvaluator(releaseInfo)
    config.templateFiles.each { updateFile(project, evaluator, it) }

    commitIfFilesChanged(project, config, releaseInfo)
  }

  def updateFile(project, evaluator, templateFile) {
    if (templateFile.lines.isEmpty()) {
      return
    }

    if (Files.notExists(Paths.get("$templateFile.inputFile"))) {
      throw new GradleException("unable to find input template file - ${templateFile.inputFile}")
    }

    def currentLineIndex = 0
    def currentLine = templateFile.lines[currentLineIndex]
    def tempFile = File.createTempFile('gradle-release-plugin-version-template', '.tmp')
    new File("$tempFile").withWriter { writer ->
      def readerLineNumber = 1
      new File("$templateFile.inputFile").eachLine { line ->
        if (currentLine != null && readerLineNumber == currentLine.lineNumber) {
          writer.println currentLine.evaluate(evaluator)
          currentLineIndex++
          currentLine = templateFile.lines[currentLineIndex]
        } else {
          writer.println line
        }
        readerLineNumber++
      }
    }

    tempFile.renameTo(new File("$templateFile.outputFile"))

    Logger.warn(project, "updated version info for ${templateFile.inputFile}")
  }

  def commitIfFilesChanged(project, config, releaseInfo) {
    def gitStatus = getRepository().getStatus(project)
    if (!gitStatus.empty) {
      Logger.warn(project, 'commiting changes to versioned files')
      getRepository().commit(project, "updated versions info in ${config.templateFiles.size()} files " +
                                      "for release v${releaseInfo.next}")
      getRepository().push(project)
    }
  }
}
