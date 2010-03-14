/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.eclipse.preferences;

import org.gradle.eclipse.GradlePlugin;

/**
 * @author Rene Groeschke
 *
 */
public interface IGradlePreferenceConstants {

	public static final String MANUELL_GRADLE_HOME = GradlePlugin.PLUGIN_ID + "_MAN_GRADLE_HOME";
	public static final String GRADLE_FIND_BUILD_FILE_NAMES = "_GRADLE_FIND_BUILD_FILE_NAMES"; //$NON-NLS-1$
	public static final String USE_SPECIFIC_GRADLE_HOME = "_USE_SPECIFIC_GRADLE_HOME";
	
	/**
	 * The symbolic names for colors for displaying the content in the Console
	 * @see org.eclipse.jface.resource.ColorRegistry
	 */
	
	public static final String CONSOLE_ERROR_COLOR = "org.gradle.eclipse.ui.errorColor"; //$NON-NLS-1$
	public static final String CONSOLE_WARNING_COLOR = "org.gradle.eclipse.ui.warningColor"; //$NON-NLS-1$
	public static final String CONSOLE_INFO_COLOR = "org.gradle.eclipse.ui.informationColor"; //$NON-NLS-1$
	public static final String CONSOLE_VERBOSE_COLOR = "org.gradle.eclipse.ui.verboseColor"; //$NON-NLS-1$
	public static final String CONSOLE_DEBUG_COLOR = "org.gradle.eclipse.ui.debugColor"; //$NON-NLS-1$	
	
	public static final String GRADLE_ERROR_DIALOG = "GRADLE_ERROR_DIALOG";
	

}
