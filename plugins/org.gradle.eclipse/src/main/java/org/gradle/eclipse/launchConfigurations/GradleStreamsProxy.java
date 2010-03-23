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

import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.gradle.eclipse.GradlePlugin;


/**
 * @author Rene Groeschke
 *
 */
public class GradleStreamsProxy implements IStreamsProxy {
	
	private GradleStreamMonitor fErrorMonitor = new GradleStreamMonitor();
	private GradleStreamMonitor fOutputMonitor = new GradleStreamMonitor();
	
	public static final String GRADLE_DEBUG_STREAM = GradlePlugin.PLUGIN_ID + ".GRADLE_DEBUG_STREAM"; //$NON-NLS-1$
	public static final String GRADLE_VERBOSE_STREAM = GradlePlugin.PLUGIN_ID + ".GRADLE_VERBOSE_STREAM"; //$NON-NLS-1$
	public static final String GRADLE_WARNING_STREAM = GradlePlugin.PLUGIN_ID  + ".GRADLE_WARNING_STREAM"; //$NON-NLS-1$
	
	private GradleStreamMonitor fDebugMonitor = new GradleStreamMonitor();
	private GradleStreamMonitor fVerboseMonitor = new GradleStreamMonitor();
	private GradleStreamMonitor fWarningMonitor = new GradleStreamMonitor();

	/**
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getErrorStreamMonitor()
	 */
	public IStreamMonitor getErrorStreamMonitor() {
		return fErrorMonitor;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getOutputStreamMonitor()
	 */
	public IStreamMonitor getOutputStreamMonitor() {
		return fOutputMonitor;
	}

	/**
	 * @see org.eclipse.debug.core.model.IStreamsProxy#write(java.lang.String)
	 */
	public void write(String input) {
		//change this: @FIXME use more sophisticated approach
		String[] split = input.split("\n");
		for(String line : split){
			GradleStreamMonitor monitor = getMonitorByLineParsing(line);
			monitor.append(line);
			monitor.append("\n");
		}
	}

	private GradleStreamMonitor getMonitorByLineParsing(String line) {
		if(line.contains(" [main] DEBUG ")){
			return fDebugMonitor;
		}else if(line.contains("[main] WARN  ")){
			return fWarningMonitor;
		}else if(line.contains("[main] INFO  ")){
			return fWarningMonitor;
		}else if(line.contains("[main] ERROR ")){
			return fErrorMonitor;
		}else if(line.contains("[main] ERROR ")){
			return fErrorMonitor;
		}
		
		return fOutputMonitor;
		
	}

	public IStreamMonitor getWarningStreamMonitor() {
		return fWarningMonitor;
	}
	
	public IStreamMonitor getDebugStreamMonitor() {
		return fDebugMonitor;
	}	
	
	public IStreamMonitor getVerboseStreamMonitor() {
		return fVerboseMonitor;
	}	
}
