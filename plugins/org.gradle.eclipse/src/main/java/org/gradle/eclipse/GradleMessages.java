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

import org.eclipse.osgi.util.NLS;

/**
 * @author Rene Groeschke
 * */
public class GradleMessages extends NLS {
	private static final String BUNDLE_NAME = "org.gradle.eclipse.GradleMessages";//$NON-NLS-1$

	protected static String INVALID_GRADLE_HOME;
	
	public static String GradleRunnerBuildFailed1;
	public static String ImageDescriptorRegistryAllocatingImageForWrongDisplay1;
	
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, GradleMessages.class);
	}
}