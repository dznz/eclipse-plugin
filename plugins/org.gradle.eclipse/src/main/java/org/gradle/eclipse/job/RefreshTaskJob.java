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
package org.gradle.eclipse.job;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.gradle.eclipse.BuildInformationCache;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.interaction.GradleProcessExecListener;
import org.gradle.eclipse.interaction.GradleRefreshRequestExecutionInteraction;
import org.gradle.foundation.ProjectView;
import org.gradle.gradleplugin.foundation.GradlePluginLord;
import org.gradle.gradleplugin.foundation.request.ExecutionRequest;
import org.gradle.gradleplugin.foundation.request.RefreshTaskListRequest;
import org.gradle.gradleplugin.foundation.request.Request;


/**
 * @author Rene Groeschke
 * This class extends AbstractGradleJob to get a list of available
 * tasks in a build file.
 */
public class RefreshTaskJob extends AbstractGradleJob{

	private final GradlePluginLord pluginLord;
	private final String buildFilePath;
	private BuildInformationCache cache;

	public RefreshTaskJob(GradlePluginLord gradlePluginLord, String buildFilePath, BuildInformationCache cache) {
		super("Calculating Gradle Tasks...");
		this.pluginLord = gradlePluginLord;
		this.buildFilePath = buildFilePath;
		this.cache = cache;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return calculateTasks(monitor);
	}
	
	/**
	 * to be able to run the refresh job in sync without job api extract the code to a package wide visible void
	 * */
	
	public IStatus calculateTasks(IProgressMonitor monitor){
		final GradleProcessExecListener executionlistener = new GradleRefreshRequestExecutionInteraction(monitor);
		
		pluginLord.startExecutionQueue();
		final BooleanHolder isComplete = new BooleanHolder();

		StringBuffer additionalCmdLine = new StringBuffer(" -b ");
		additionalCmdLine.append(getBuildFileName());
		
		GradlePluginLord.RequestObserver observer = new GradlePluginLord.RequestObserver() {
	           
			public void executionRequestAdded( ExecutionRequest request )
	           {
	              request.setExecutionInteraction( executionlistener );
	           }
	           public void refreshRequestAdded( RefreshTaskListRequest request ) { 
	           }
	           public void aboutToExecuteRequest( Request request ) { 
	           }

	           public void requestExecutionComplete( Request request, int result, String output ) {
	        	   isComplete.setValue(true);
	           }
	        };

	    pluginLord.addRequestObserver(observer, false);
	    pluginLord.addRefreshRequestToQueue(additionalCmdLine.toString());
		//keep job open til listener reports gradle has finished
		while(!isComplete.getValue()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return new Status(IStatus.WARNING, GradlePlugin.PLUGIN_ID, "Error while recalculating Gradle Tasks", e);
			}
		}
		List<ProjectView> projects = pluginLord.getProjects();
		cache.put(buildFilePath, projects);
		if(executionlistener.getThrowable()!=null){
			return new Status(IStatus.WARNING, GradlePlugin.PLUGIN_ID, "Error while recalculating Gradle Tasks");
		}
		return Status.OK_STATUS;	
	}

	private String getBuildFileName() {
		return new File(buildFilePath).getName();
	}
}
