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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.gradle.eclipse.GradleExecScheduler;


/**
 * @author Rene Groeschke
 *
 */
public class GradleRunner {

	private ILaunchConfiguration configuration;
	private StringBuffer commandLine;
	private ILaunch launch;

	public GradleRunner(ILaunchConfiguration configuration, ILaunch launch, StringBuffer commandLine) throws CoreException {
		this.launch = launch;
		this.configuration = configuration;
		this.commandLine = commandLine;
	}
	
	public void run(IProgressMonitor monitor) throws CoreException{
		monitor.beginTask("Invoking Gradle", 100);
//		Long start = System.currentTimeMillis();
		monitor.worked(5);
		GradleExecScheduler.getInstance().startGradleBuildRun(configuration, commandLine, getProcess());
		monitor.done();
	}

	private GradleProcess getProcess() {
		IProcess[] processes = launch.getProcesses();
		for(IProcess process : processes){
			if(process instanceof GradleProcess){
				return ((GradleProcess)process);
			}
		}
		return null;
	}
}
