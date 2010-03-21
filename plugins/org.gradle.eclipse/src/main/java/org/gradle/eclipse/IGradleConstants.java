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
package org.gradle.eclipse;

import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

/**
 * @author Rene Groeschke
 */
@SuppressWarnings("restriction")
public class IGradleConstants {

	public static final String PLUGIN_ID = "org.gradle.eclipse";

	/**
	 * String attribute identifying the location of an external. Default value
	 * is <code>null</code>. Encoding is tool specific.
	 */
	
	/**
	 * central storage of restricted key "IExternalToolConstants.ATTR_LOCATION"
	 * */
	public static final String ATTR_LOCATION = IExternalToolConstants.ATTR_LOCATION;
	
	public static final String ATTR_TOOL_ARGUMENTS = PLUGIN_ID + ".ATTR_TOOL_ARGUMENTS";

	
	public static final String IMG_GRADLE_ECLIPSE_RUNTIME_OBJECT = PLUGIN_ID + ".gradleEclipse"; //$NON-NLS-1$

	public static final String IMG_TAB_CLASSPATH = PLUGIN_ID + ".IMG_TAB_CLASSPATH"; //$NON-NLS-1$;

	/**
	 * Status code used by the 'Run Gradle' status handler which is invoked when
	 * the launch dialog is opened by the 'Run Gradle' action.
	 */
	public static final int STATUS_INIT_RUN_GRADLE = 1000;

	/**
	 * Key String for the tasks tab image icon in gradle launch dialog 
	 * */
	public static final String IMG_TAB_GRADLE_TASKS = PLUGIN_ID + ".IMG_TAB_GRADLE_TASKS"; //$NON-NLS-1$;

	/**
	 * Key String for gradle task image icon
	 * */
	public static final String IMG_GRADLE_TASK = PLUGIN_ID + ".IMG_GRADLE_TASK"; //$NON-NLS-1$;

	/**
	 * Key String for gradle default task image icon
	 * */
	public static final String IMG_GRADLE_DEFAULT_TASK = PLUGIN_ID	+ ".IMG_GRADLE_DEFAULT_TASK"; //$NON-NLS-1$;

	public static final String GRADLE_TASKS_ATTRIBUTES = PLUGIN_ID 	+ ".GRADLE_TASKS_ATTRIBUTES"; //$NON-NLS-1$;

	public static final String ID_GRADLE_PROCESS_TYPE = "org.gradle.eclipse.gradleProcess"; //$NON-NLS-1$;


}
