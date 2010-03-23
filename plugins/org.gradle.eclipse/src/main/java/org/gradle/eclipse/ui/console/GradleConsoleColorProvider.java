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
package org.gradle.eclipse.ui.console;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.launchConfigurations.GradleProcess;
import org.gradle.eclipse.launchConfigurations.GradleStreamsProxy;
import org.gradle.eclipse.preferences.IGradlePreferenceConstants;


/**
 * @author Rene Groeschke
 *
 */
public class GradleConsoleColorProvider extends ConsoleColorProvider implements IPropertyChangeListener {

 	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#getColor(java.lang.String)
	 */
	public Color getColor(String streamIdentifer) {
		if (streamIdentifer.equals(IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM)) {
			return GradlePlugin.getPreferenceColor(IGradlePreferenceConstants.CONSOLE_INFO_COLOR);
		}
		if (streamIdentifer.equals(IDebugUIConstants.ID_STANDARD_ERROR_STREAM)) {
			return GradlePlugin.getPreferenceColor(IGradlePreferenceConstants.CONSOLE_ERROR_COLOR);
		}				
		if (streamIdentifer.equals(GradleStreamsProxy.GRADLE_DEBUG_STREAM)) {
			return GradlePlugin.getPreferenceColor(IGradlePreferenceConstants.CONSOLE_DEBUG_COLOR);
		}
		if (streamIdentifer.equals(GradleStreamsProxy.GRADLE_VERBOSE_STREAM)) {
			return GradlePlugin.getPreferenceColor(IGradlePreferenceConstants.CONSOLE_VERBOSE_COLOR);
		}
		if (streamIdentifer.equals(GradleStreamsProxy.GRADLE_WARNING_STREAM)) {
			return GradlePlugin.getPreferenceColor(IGradlePreferenceConstants.CONSOLE_WARNING_COLOR);
		}
		return super.getColor(streamIdentifer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#connect(org.eclipse.debug.core.model.IProcess, org.eclipse.debug.ui.console.IConsole)
	 */
	public void connect(IProcess process, IConsole console) {
		GradleStreamsProxy proxy = (GradleStreamsProxy)process.getStreamsProxy();
		if (process instanceof GradleProcess) {
			((GradleProcess)process).setConsole(console);
		}
		if (proxy != null) {
			console.connect(proxy.getDebugStreamMonitor(), GradleStreamsProxy.GRADLE_DEBUG_STREAM);
			console.connect(proxy.getWarningStreamMonitor(), GradleStreamsProxy.GRADLE_WARNING_STREAM);
			console.connect(proxy.getVerboseStreamMonitor(), GradleStreamsProxy.GRADLE_VERBOSE_STREAM);
		}
		
		super.connect(process, console);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#isReadOnly()
	 */
	public boolean isReadOnly() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
	    final String streamId = getStreamId(event.getProperty());
		if (streamId != null) {
			GradlePlugin.getStandardDisplay().asyncExec(new Runnable() {
				public void run() {
				    IOConsoleOutputStream stream = getConsole().getStream(streamId);
				    if (stream != null) {
				        stream.setColor(getColor(streamId));
				    }
				}
			});
		}
	}

	private String getStreamId(String colorId) {
		if (IGradlePreferenceConstants.CONSOLE_DEBUG_COLOR.equals(colorId)) {
			return GradleStreamsProxy.GRADLE_DEBUG_STREAM;
		} else if (IGradlePreferenceConstants.CONSOLE_ERROR_COLOR.equals(colorId)) {
			return IDebugUIConstants.ID_STANDARD_ERROR_STREAM;
		} else if (IGradlePreferenceConstants.CONSOLE_INFO_COLOR.equals(colorId)) {
			return IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM;
		} else if (IGradlePreferenceConstants.CONSOLE_VERBOSE_COLOR.equals(colorId)) {
			return GradleStreamsProxy.GRADLE_VERBOSE_STREAM;
		} else if (IGradlePreferenceConstants.CONSOLE_WARNING_COLOR.equals(colorId)) {
			return GradleStreamsProxy.GRADLE_WARNING_STREAM;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#disconnect()
	 */
	public void disconnect() {
		super.disconnect();
		GradlePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}
}