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
package net.anshulverma.gradle.release.info

import static net.anshulverma.gradle.release.info.PropertyName.SKIP_TEMPLATE_VALIDATION
import groovy.text.SimpleTemplateEngine
import net.anshulverma.gradle.release.version.ReleaseType
import net.anshulverma.gradle.release.version.VersioningStrategy
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
class ReleaseInfoTemplateEvaluator {

  private static final TEMPLATE_ENGINE = new SimpleTemplateEngine()

  private final ProjectPropertyReader propertyReader
  private final Map binding
  private final Map validationBinding

  ReleaseInfoTemplateEvaluator(Project project, ReleaseInfo releaseInfo) {
    propertyReader = new ProjectPropertyReader(project)

    binding = [
        releaseType             : "$releaseInfo.releaseType",
        isRelease               : releaseInfo.isRelease,
        currentVersion          : releaseInfo.current,
        currentVersionWithSuffix: "$releaseInfo.current${releaseInfo.isRelease ? '' : '-SNAPSHOT'}",
        nextVersion             : releaseInfo.next,
        author                  : "$releaseInfo.author"
    ]

    // binding for last version check
    validationBinding = [
        releaseType             : "(${ReleaseType.values().join('|')})",
        isRelease               : '(true|false)',
        currentVersion          : "${VersioningStrategy.VERSION_REGEX}",
        currentVersionWithSuffix: "${VersioningStrategy.VERSION_REGEX}",
        nextVersion             : "${VersioningStrategy.VERSION_REGEX}",
        author                  : '[a-zA-Z0-9_]+'
    ]
  }

  String evaluate(String template, String currentLine = null) {
    def evaluated = TEMPLATE_ENGINE.createTemplate("$template").make(binding).toString()
    if (currentLine != null && !propertyReader.templateValidationDisabled) {
      validateCurrentLine(template, currentLine)
    }
    evaluated
  }

  private def validateCurrentLine(template, currentLine) {
    def validationRegex = TEMPLATE_ENGINE.createTemplate("$template").make(validationBinding).toString()
    assert (currentLine =~ /^${validationRegex}$/).find(): "Validation precheck failed as line \"${currentLine}\" " +
        "does not match template \"${template}\". " +
        'To avoid this error:\n' +
        '1. Verify version templates are correct\n' +
        '2. Re-run last task with flag: ' +
        "-P${SKIP_TEMPLATE_VALIDATION.name}=true\n" +
        'Once you follow the above steps this error will ' +
        'disappear and the versioned templates will also be fixed'
  }
}
