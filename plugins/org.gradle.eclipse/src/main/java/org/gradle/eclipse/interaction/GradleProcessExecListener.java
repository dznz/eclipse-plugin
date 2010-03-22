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

import org.eclipse.core.runtime.IProgressMonitor;
import org.gradle.foundation.ipc.gradle.ExecuteGradleCommandServerProtocol.ExecutionInteraction;

/**
 * @author Rene Groeschke
 *
 */
public abstract class GradleProcessExecListener implements
		ExecutionInteraction {

	private IProgressMonitor monitor;
	protected boolean finished = false;
	protected Throwable throwable = null;
	protected boolean successful;
	private String message;
	
	public String getMessage() {
		return message;
	}

	public boolean isFinished() {
		return finished;
	}

	public GradleProcessExecListener(IProgressMonitor monitor) {
		this.monitor = monitor;
		
	}

	public void reportExecutionFinished(boolean success, String arg1,
			Throwable thrown) {
		finished = true;
		throwable = thrown;
		successful = success;
		message = arg1;
		worked(10);
	}

	

	protected void worked(int worked) {
		if(monitor!=null){
			monitor.worked(worked);
		}
	}
	
	protected void beginTask(String task, int totalWork) {
		if(monitor!=null){
			monitor.beginTask(task, totalWork);
		}
	}

	protected void subTask(String task) {
		if(monitor!=null){
			monitor.subTask(task);
		}
	}
	/*
	 * returns a possibly throwable thrown by the gradle process or null if no throwable was thrown by gradle
	 * 
	 * */
	public Throwable getThrowable() {
		return throwable;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
