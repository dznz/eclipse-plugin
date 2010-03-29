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
package org.gradle.eclipse.launchConfigurations;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.gradle.eclipse.IGradleConstants;

/**
 * @author Rene Groeschke
 *
 */
public class GradleTabGroup extends AbstractLaunchConfigurationTabGroup {
    
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
			new GradleMainTab(),
			new GradleTasksTab(),
			new CommonTab()
		};
		setTabs(tabs);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 * 
	 * called when new execution configuration is created
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		//set default name for script
		IResource resource = DebugUITools.getSelectedResource();
		if (resource != null && resource instanceof IFile) {
			IFile file = (IFile)resource;
			String extension = file.getFileExtension();
			if (extension != null && extension.equalsIgnoreCase("gradle")) { //$NON-NLS-1$
				String projectName= file.getProject().getName();
				StringBuffer buffer = new StringBuffer(projectName);
				buffer.append(' ');
				buffer.append(file.getName());
				String name = buffer.toString().trim();
				name= DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(name);
				configuration.rename(name);
				//set the project name so that the correct default VM install can be determined
				configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
				
				String buildlocation = VariablesPlugin.getDefault().getStringVariableManager()
													  .generateVariableExpression("workspace_loc", file.getFullPath().toString());//$NON-NLS-1$
				configuration.setAttribute(IGradleConstants.ATTR_LOCATION, buildlocation); 
			}		
		}
		
		super.setDefaults(configuration);
	}
}