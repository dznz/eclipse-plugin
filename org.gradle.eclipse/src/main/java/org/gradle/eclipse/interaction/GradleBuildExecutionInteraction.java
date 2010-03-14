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

package org.gradle.eclipse.interaction;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.launchConfigurations.GradleProcess;
import org.gradle.foundation.ipc.gradle.ExecuteGradleCommandServerProtocol;

/**
 * @author Rene Groeschke
 * 
 * Default - Implementation of the ExecuteGradleCommandServerProtocol.ExecutionInteraction Interface,
 * this class managed the interaction between the created gradle process and the eclipse IDE.
 * */
public class GradleBuildExecutionInteraction extends GradleProcessExecListener{
	private GradleProcess process = null;
	
	public GradleBuildExecutionInteraction(IProgressMonitor monitor, GradleProcess gradleProcess) {
		super(monitor);
		this.process = gradleProcess;
	}

	
	public void reportExecutionFinished(boolean arg0, String arg1,
			Throwable arg2) {	
		super.reportExecutionFinished(arg0, arg1, arg2);
		process.terminated();
	}

	/**
	 * @see ExecuteGradleCommandServerProtocol.ExecutionInteraction#reportExecutionStarted()
	 * */
	public void reportExecutionStarted() {
		beginTask("Executing Gradle Build", 100);
		worked(10);
	}

	/**
	 * @see ExecuteGradleCommandServerProtocol.ExecutionInteraction#reportLiveOutput(String)
	 * */
	public void reportLiveOutput(String arg0) {
		if(process!=null){
			try {
				process.getStreamsProxy().write(arg0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see ExecuteGradleCommandServerProtocol.ExecutionInteraction#reportTaskComplete(String, float)
	 * */
	public void reportTaskComplete(String arg0, float arg1) {
		worked(10);
	}

	/**
	 * @see ExecuteGradleCommandServerProtocol.ExecutionInteraction#reportTaskStarted(String, float)
	 * */
	public void reportTaskStarted(String arg0, float arg1) {
		subTask("Running Task :" + arg0);
	}


	public void reportNumberOfTasksToExecute(int arg0) {
		try {
			process.getStreamsProxy().write("running #tasks: " + Integer.toString(arg0));
		} catch (IOException e) {
			GradlePlugin.log(e);
		}
	}
};