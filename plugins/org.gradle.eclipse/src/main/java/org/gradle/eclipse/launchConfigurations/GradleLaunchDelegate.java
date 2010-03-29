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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.IGradleConstants;
import org.gradle.eclipse.preferences.IGradlePreferenceConstants;

import com.ibm.icu.text.MessageFormat;


/**
 * @author Rene Groeschke
 */
public class GradleLaunchDelegate extends LaunchConfigurationDelegate  {

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled()) {
			return;
		}
		
		if (!CommonTab.isLaunchInBackground(configuration)) {
			monitor.beginTask(MessageFormat.format(GradleLaunchConfigurationMessages.GradleLaunchDelegate_Launching__0__1, new String[] {configuration.getName()}), 10);
		} else {
			if(mode.equals("run")){
				monitor.beginTask(MessageFormat.format(GradleLaunchConfigurationMessages.GradleLaunchDelegate_Running__0__2, new String[] {configuration.getName()}), 100);				
			}else{
				//mode is debug
				monitor.beginTask(MessageFormat.format(GradleLaunchConfigurationMessages.GradleLaunchDelegate_Debugging__0__2, new String[] {configuration.getName()}), 100);				
			}
		}
		if (monitor.isCanceled()) {
			return;
		}

		
		StringBuffer cmdLine = new StringBuffer(configuration.getAttribute(IGradleConstants.ATTR_TOOL_ARGUMENTS, ""));
		cmdLine.append(" ").append(configuration.getAttribute(IGradleConstants.GRADLE_TASKS_ATTRIBUTES, ""));
		
		//add debug flag if mode is DEBUG
		if(mode.equals("debug")){
			cmdLine.append(" ").append("-d");
		}
		monitor.worked(1);
		runGradleBuild(configuration, launch, monitor, "idStamp" + System.currentTimeMillis(), cmdLine);
		monitor.worked(1);
		if (monitor.isCanceled()) {
			return;
		}
		monitor.done();
	}
	
	private void runGradleBuild(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor, String idStamp, StringBuffer cmdLine) throws CoreException {
		Map<String, String> attributes= new HashMap<String, String>(2);
		attributes.put(IProcess.ATTR_PROCESS_TYPE, IGradleConstants.ID_GRADLE_PROCESS_TYPE);
		final GradleProcess process = new GradleProcess("GradleProcess", launch, attributes);
		
		GradleRunner runner = new GradleRunner(configuration, launch, cmdLine);
		try {
			runner.run(monitor);
		} catch (CoreException e) {
			process.terminated();
			e.printStackTrace();
			handleException(e, GradleLaunchConfigurationMessages.GradleLaunchDelegate_23);
			return;
		}
		RefreshTab.refreshResources(configuration, monitor);
	}
	
	private void handleException(final CoreException e, final String title) {
		IPreferenceStore store= GradlePlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(IGradlePreferenceConstants.GRADLE_ERROR_DIALOG)) {
			GradlePlugin.getStandardDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialogWithToggle.openError(null, title, e.getMessage(), GradleLaunchConfigurationMessages.GradleLaunchDelegate_22, false, GradlePlugin.getDefault().getPreferenceStore(), IGradlePreferenceConstants.GRADLE_ERROR_DIALOG);
				}
			});
		}
	}
}