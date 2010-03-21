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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.interaction.GradleBuildExecutionInteraction;
import org.gradle.eclipse.interaction.GradleProcessExecListener;
import org.gradle.eclipse.launchConfigurations.GradleProcess;
import org.gradle.gradleplugin.foundation.GradlePluginLord;
import org.gradle.gradleplugin.foundation.request.ExecutionRequest;
import org.gradle.gradleplugin.foundation.request.RefreshTaskListRequest;
import org.gradle.gradleplugin.foundation.request.Request;


public class BuildJob extends AbstractGradleJob {

	private final GradlePluginLord pluginLord;
	private final GradleProcess process;
	private final ILaunchConfiguration configuration;
	private final String commandLine;

	public BuildJob(GradlePluginLord gradlePluginLord, GradleProcess process,
			ILaunchConfiguration configuration, String commandLine) {

		super("Running Gradle Build...");
		this.pluginLord = gradlePluginLord;
		this.process = process;
		this.configuration = configuration;
		this.commandLine = commandLine;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		final GradleProcessExecListener executionlistener = new GradleBuildExecutionInteraction(
				monitor, process);
		pluginLord.startExecutionQueue();

		final BooleanHolder isComplete = new BooleanHolder();
		// /
		GradlePluginLord.RequestObserver observer = new GradlePluginLord.RequestObserver() {
			public void executionRequestAdded(ExecutionRequest request) {
				request.setExecutionInteraction(executionlistener);
			}

			public void refreshRequestAdded(RefreshTaskListRequest request) {

			}

			public void aboutToExecuteRequest(Request request) {

			}

			public void requestExecutionComplete(Request request, int result,
					String output) {
				isComplete.setValue(true);
			}
		};

		// add the observer before we add the request due to timing issues.
		// It's possible for it to completely execute before we return from
		// addExecutionRequestToQueue.

		pluginLord.addRequestObserver(observer, false);
		pluginLord.addExecutionRequestToQueue(commandLine, configuration.getName());

		// gradlePluginLord.addExecutionRequestToQueue(commandLine,
		// executionlistener);
		// keep job open til listener reports gradle has finished
		while (!isComplete.getValue()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return new Status(IStatus.WARNING, GradlePlugin.PLUGIN_ID,
						"Error while running Gradle Tasks", e);
			}
		}

		if (executionlistener.getThrowable() != null) {
			return new Status(IStatus.WARNING, GradlePlugin.PLUGIN_ID,
					"Error while running Gradle Tasks", executionlistener
							.getThrowable());
		}
		return Status.OK_STATUS;
	}
}
