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
package net.anshulverma.gradle.release.version.template

import net.anshulverma.gradle.release.info.ReleaseInfoTemplateEvaluator

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class CustomVersionInfoTemplateEvaluationTask extends AbstractTemplateEvaluationTask {

  private final VersionTemplateConfig templateConfig

  CustomVersionInfoTemplateEvaluationTask(VersionTemplateConfig templateConfig) {
    this.templateConfig = templateConfig
  }

  def evaluate(File inputFile, ReleaseInfoTemplateEvaluator evaluator, Writer writer) {
    def currentTemplateLineIndex = 0
    def readerLineNumber = 1
    def currentTemplateLineInfo = templateConfig.getLine(currentTemplateLineIndex)
    try {
      inputFile.eachLine { line ->
        if (currentTemplateLineInfo != null && readerLineNumber == currentTemplateLineInfo.lineNumber) {
          writer.println evaluator.evaluate(currentTemplateLineInfo.template, line)
          currentTemplateLineIndex++
          currentTemplateLineInfo = templateConfig.getLine(currentTemplateLineIndex)
        } else if (templateConfig.isInputFromTemplate()) {
          writer.println evaluator.evaluate(line)
        } else {
          writer.println line
        }
        readerLineNumber++
      }
    } catch (AssertionError t) {
      throw new IllegalStateException("unable to evaluate version template for $inputFile. Reason: ${t.message}", t)
    }
  }

}
