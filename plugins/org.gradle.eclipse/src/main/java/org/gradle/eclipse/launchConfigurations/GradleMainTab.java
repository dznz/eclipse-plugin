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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab;
import org.eclipse.ui.externaltools.internal.model.ExternalToolsPlugin;
import org.eclipse.ui.externaltools.internal.ui.FileSelectionDialog;
import org.gradle.eclipse.IGradleConstants;

/**
 * @author Rene Groeschke
 *
 */
@SuppressWarnings("restriction")
public class GradleMainTab extends ExternalToolsMainTab {

	private IFile fNewFile;

    /* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(gridData);
		mainComposite.setFont(parent.getFont());
		createLocationComponent(mainComposite);
		createArgumentComponent(mainComposite);
		createVerticalSpacer(mainComposite, 2);
		Dialog.applyDialogFont(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab#handleWorkspaceLocationButtonSelected()
	 */
	protected void handleWorkspaceLocationButtonSelected() {
		FileSelectionDialog dialog;
		dialog = new FileSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), GradleLaunchConfigurationMessages.GradleMainTab__Select_a_build_file__1);
		dialog.open();
		IStructuredSelection result = dialog.getResult();
		if (result == null) {
			return;
		}
		Object file= result.getFirstElement();
		if (file instanceof IFile) {
            fNewFile= (IFile)file;
			locationField.setText(VariablesPlugin.getDefault().getStringVariableManager().generateVariableExpression("workspace_loc", fNewFile.getFullPath().toString())); //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab#getLocationLabel()
	 */
	protected String getLocationLabel() {
		return GradleLaunchConfigurationMessages.GradleMainTab_1;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String location= locationField.getText().trim();
		if (location.length() == 0) {
			configuration.setAttribute(IGradleConstants.ATTR_LOCATION, (String)null);
		} else {
			configuration.setAttribute(IGradleConstants.ATTR_LOCATION, location);
		}

		String arguments= argumentField.getText().trim();
		if (arguments.length() == 0) {
			configuration.setAttribute(IGradleConstants.ATTR_TOOL_ARGUMENTS, (String)null);
		} else {
			configuration.setAttribute(IGradleConstants.ATTR_TOOL_ARGUMENTS, arguments);
		}
	}
	
	/**
	 * Updates the location widgets to match the state of the given launch
	 * configuration.
	 */
	protected void updateLocation(ILaunchConfiguration configuration) {
		String location= ""; //$NON-NLS-1$
		try {
			location= configuration.getAttribute(IGradleConstants.ATTR_LOCATION, ""); //$NON-NLS-1$
		} catch (CoreException ce) {
			ExternalToolsPlugin.getDefault().log(GradleLaunchConfigurationMessages.GradleMainTab_Error_reading_configuration, ce);
		}
		locationField.setText(location);
	}
	
	//empty method needed to hide working directory
	protected void updateWorkingDirectory(ILaunchConfiguration configuration) {
//		super.updateWorkingDirectory(configuration);
	}
	
	protected boolean validateWorkDirectory(){
		return true;
	}
}
