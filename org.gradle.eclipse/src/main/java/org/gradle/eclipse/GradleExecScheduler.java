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

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.gradle.eclipse.job.BuildJob;
import org.gradle.eclipse.job.RefreshTaskJob;
import org.gradle.eclipse.launchConfigurations.GradleProcess;
import org.gradle.foundation.ProjectView;
import org.gradle.gradleplugin.foundation.CommandLineArgumentAlteringListener;
import org.gradle.gradleplugin.foundation.GradlePluginLord;


/**
 * @author Rene Groeschke
 * */
public class GradleExecScheduler {

	private static GradleExecScheduler instance = null;

	/**
	 * The Gradle Scheduler manages the lifecycle of the buildinformation cache
	 * */
	private BuildInformationCache cache;
	
	
	public static GradleExecScheduler getInstance() {
		if(instance==null){
			instance = new GradleExecScheduler();
		}
		return instance;
	}
	
	private GradleExecScheduler(){
		this.cache = new BuildInformationCache();
	}



	// these both methods should be moved to BuildInformationCache
	public List<ProjectView> getProjectViews(IFile buildFile){
		String absolutePath = new File(buildFile.getFullPath().toString()).getAbsolutePath();	
		return getProjectViews(absolutePath);
	}
	
	public List<ProjectView> getProjectViews(String absolutePath) {
		if(cache.get(absolutePath)==null){
			refreshTaskView(absolutePath, true);
		}
		return cache.get(absolutePath);
	}

	public void refreshTaskView(final String absolutePath, boolean synched) {
		if(absolutePath==null || !absolutePath.isEmpty()){
			final File absoluteDirectory = new File(absolutePath).getParentFile();
			
			if(absoluteDirectory.exists()){
				//run gradle only if directory exists
				final GradlePluginLord gradlePluginLord = new GradlePluginLord();
				gradlePluginLord.setGradleHomeDirectory(new File(GradlePlugin.getPlugin().getGradleHome()));
				gradlePluginLord.setCurrentDirectory(absoluteDirectory);
//				gradlePluginLord.
//				gradlePluginLord.addCommandLineArgumentAlteringListener(new CommandLineArgumentAlteringListener(){
//
//					public String getAdditionalCommandLineArguments(String arg0) {
//						// TODO Auto-generated method stub
//						return null;
//					}
//					
//				});
				RefreshTaskJob job = new RefreshTaskJob(gradlePluginLord, absolutePath, cache);
				
				if(!synched){
					job.setUser(false);
					job.setPriority(Job.LONG);
					job.schedule(); // start as soon as possible
				}
				else{
					job.calculateTasks(null);
				}			
			}
		}
	}
	
	public void startGradleBuildRun(final ILaunchConfiguration configuration, final StringBuffer commandLine, final GradleProcess gradleProcess) throws CoreException{
		final GradlePluginLord gradlePluginLord = new GradlePluginLord();
		gradlePluginLord.setGradleHomeDirectory(new File(GradlePlugin.getPlugin().getGradleHome()));
		String buildfilePath = configuration.getAttribute(IGradleConstants.ATTR_LOCATION, "");
		File buildFile = new File(VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(buildfilePath));
		if(buildFile==null || !buildFile.exists()){
			throw(new CoreException(new Status(IStatus.ERROR, 
											   IGradleConstants.PLUGIN_ID, 
											   "buildPath: [ " + buildfilePath + "] cannot be resolved")
				));
		}
		
		//buildfile could have any custom name so use -b flag
		commandLine.append(" -b ").append(buildFile.getName());
		gradlePluginLord.setCurrentDirectory(buildFile.getParentFile());
		
		
		// create gradle build job
		Job job = new BuildJob(gradlePluginLord, gradleProcess, configuration, commandLine.toString());
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule(); // start as soon as possible
	}
}
