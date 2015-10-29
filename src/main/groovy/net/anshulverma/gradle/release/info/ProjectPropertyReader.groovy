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

import groovy.transform.TypeChecked
import org.gradle.api.Project

/**
 * @author Anshul Verma (anshul.verma86@gmail.com)
 */
@TypeChecked
class ProjectPropertyReader {

  private static final String TRUE = 'true'
  private static final String FALSE = 'false'

  private final Project project

  ProjectPropertyReader(Project project) {
    this.project = project
  }

  boolean isAllChecksDisabled() {
    getBooleanProperty(PropertyName.SKIP_ALL_CHECKS)
  }

  boolean isBranchCheckDisabled() {
    allChecksDisabled || getBooleanProperty(PropertyName.SKIP_BRANCH_CHECK)
  }

  boolean isCleanWorkspaceCheckDisabled() {
    allChecksDisabled || getBooleanProperty(PropertyName.SKIP_CLEAN_WORKSPACE_CHECK)
  }

  boolean getBooleanProperty(PropertyName propertyName, boolean defaultValue = false) {
    Boolean.valueOf(getStringProperty(propertyName, defaultValue ? TRUE : FALSE))
  }

  String getStringProperty(PropertyName propertyName, String defaultValue = '') {
    project.hasProperty(propertyName.name) ? project.property(propertyName.name) : defaultValue
  }
}
