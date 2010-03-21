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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.gradle.eclipse.GradleExecScheduler;
import org.gradle.eclipse.GradleImages;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.IGradleConstants;
import org.gradle.eclipse.model.GradleTaskModelContentProvider;
import org.gradle.eclipse.model.GradleTaskModelLabelProvider;
import org.gradle.eclipse.util.GradleUtil;
import org.gradle.foundation.ProjectView;
import org.gradle.foundation.TaskView;


/**
 * @author Rene Groeschke
 *
 */
public class GradleTasksTab extends AbstractLaunchConfigurationTab implements IPropertyChangeListener {

	private CheckboxTableViewer fTableViewer = null;
	private List<TaskView> fAllTasks;
	private ILaunchConfiguration launchConfiguration;
	private List<ProjectView> allProjects = null;
	private List<TaskView> defaultTasks = new ArrayList<TaskView>();
	private ProjectView project;
	
	private List<TaskView> selectedTasks = new ArrayList<TaskView>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		comp.setLayout(topLayout);		
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		comp.setFont(font);
		
		createTasksTable(comp);
		
		Composite buttonComposite= new Composite(comp, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComposite.setLayout(layout);
		buttonComposite.setFont(font);
		
		createVerticalSpacer(comp, 1);
		Dialog.applyDialogFont(parent);
	}
	
	/**
	 * Return the number of rows available in the current display using the
	 * current font.
	 * @param parent The Composite whose Font will be queried.
	 * @return int The result of the display size divided by the font size.
	 */
	private int availableRows(Composite parent) {

		int fontHeight = (parent.getFont().getFontData())[0].getHeight();
		int displayHeight = parent.getDisplay().getClientArea().height;

		return displayHeight / fontHeight;
	}
	
	/**
	 * Creates the table which displays the available tasks
	 * @param parent the parent composite
	 */
	private void createTasksTable(Composite parent) {
		Font font= parent.getFont();
		Label label = new Label(parent, SWT.NONE);
		label.setFont(font);
		label.setText(GradleLaunchConfigurationMessages.GradleTasksTab_Check_task_to_e_xecute__1);
				
		final Table table= new Table(parent, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION );
		
		GridData data= new GridData(GridData.FILL_BOTH);
		int availableRows= availableRows(parent);
		data.heightHint = table.getItemHeight() * (availableRows / 20);
		data.widthHint = 500;
		data.minimumWidth = 500;
		
		table.setLayoutData(data);
		table.setFont(font);	
		table.setHeaderVisible(true);
		table.setLinesVisible(true);		

		TableLayout tableLayout= new TableLayout();
		ColumnWeightData weightData = new ColumnWeightData(250, true);
		tableLayout.addColumnData(weightData);
		weightData = new ColumnWeightData(250, true);
		tableLayout.addColumnData(weightData);		
		table.setLayout(tableLayout);

		final TableColumn column1= new TableColumn(table, SWT.NULL);
		column1.setText(GradleLaunchConfigurationMessages.GradleTasksTab_Name_5);
		column1.setWidth(300);
		
		final TableColumn column2= new TableColumn(table, SWT.NULL);
		column2.setText(GradleLaunchConfigurationMessages.GradleTasksTab_Description_6);
		column2.setWidth(300);

		//TableLayout only sizes columns once. If showing the tasks
		//tab as the initial tab, the dialog isn't open when the layout
		//occurs and the column size isn't computed correctly. Need to
		//recompute the size of the columns once all the parent controls 
		//have been created/sized.
		//HACK Bug 139190 
		getShell().addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent e) {
				if(!table.isDisposed()) {
					int tableWidth = table.getSize().x;
					if (tableWidth > 0) {
						int c1 = tableWidth / 3;
						column1.setWidth(c1);
						column2.setWidth(tableWidth - c1);
					}
					getShell().removeShellListener(this);
				}
			}
		});
		
		fTableViewer = new  CheckboxTableViewer(table);

		fTableViewer.setLabelProvider(new GradleTaskModelLabelProvider());
		fTableViewer.setContentProvider(new GradleTaskModelContentProvider());

		fTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateOrderedTargets(event.getElement(), event.getChecked());
			}
		});
	}

	
	/**
	 * Updates the ordered targets list in response to an element being checked
	 * or unchecked. When the element is checked, it's added to the list. When
	 * unchecked, it's removed.
	 * 
	 * @param element the element in question
	 * @param checked whether the element has been checked or unchecked
	 */
	private void updateOrderedTargets(Object element , boolean checked) {
		if (checked) {
			 selectedTasks.add((TaskView)element);
		} else {
			selectedTasks.remove(element);
		}	 
		updateLaunchConfigurationDialog();	
	}
	
	/**
	 * Returns all tasks in the buildfile.
	 * @return all tasks in the buildfile
	 */
	private List<TaskView> getTasks() {
		if (fAllTasks == null || fAllTasks.isEmpty() || isDirty()) {

			setDirty(false);
			setErrorMessage(null);
			setMessage(null);
			
			final CoreException[] exceptions= new CoreException[1];
			try {
				
				IRunnableContext context= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (context == null) {
				    context= getLaunchConfigurationDialog();
				}

				if (!ResourcesPlugin.getWorkspace().isTreeLocked()) {
					//only set a scheduling rule if not in a resource change callback
					String variableString = launchConfiguration.getAttribute(IGradleConstants.ATTR_LOCATION, "");
					if(!variableString.isEmpty()){
						ISchedulingRule rule= null;
						final String absFileLocation = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(variableString);
						rule = GradleUtil.getFileForLocation(absFileLocation, null);
						
						IRunnableWithProgress operation= new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) {
								allProjects = GradleExecScheduler.getInstance().getProjectViews(absFileLocation);
							}
						};
						PlatformUI.getWorkbench().getProgressService().runInUI(context, operation, rule);
					}
				}
			}catch (CoreException e) {
			    GradlePlugin.log("Internal error occurred retrieving targets", e); //$NON-NLS-1$
			    setErrorMessage(GradleLaunchConfigurationMessages.GradleTasksTab_1);
			    return null;
			} 
			catch (InvocationTargetException e) {
			    GradlePlugin.log("Internal error occurred retrieving targets", e.getTargetException()); //$NON-NLS-1$
			    setErrorMessage(GradleLaunchConfigurationMessages.GradleTasksTab_1);
			    return null;
			} catch (InterruptedException e) {
			    GradlePlugin.log("Internal error occurred retrieving targets", e); //$NON-NLS-1$
			    setErrorMessage(GradleLaunchConfigurationMessages.GradleTasksTab_1);
			    return null;
			}
			
			if (exceptions[0] != null) {
				IStatus exceptionStatus= exceptions[0].getStatus();
				IStatus[] children= exceptionStatus.getChildren();
				StringBuffer message= new StringBuffer(exceptions[0].getMessage());
				for (int i = 0; i < children.length; i++) {
					message.append(' ');
					IStatus childStatus = children[i];
					message.append(childStatus.getMessage());
				}
				setErrorMessage(message.toString());
				return new ArrayList<TaskView>();
			}
			
			if (allProjects == null) {
			    //if an error was not thrown during parsing then having no task is valid
			    return  new ArrayList<TaskView>();
			}
			
			project = allProjects.get(0);
			defaultTasks = project.getDefaultTasks();
			fAllTasks = project.getTasks();
		}
		
		return fAllTasks;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return GradleLaunchConfigurationMessages.GradleTasksTab_1;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return GradleImages.getImage(IGradleConstants.IMG_TAB_GRADLE_TASKS);
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		launchConfiguration = configuration;
		fAllTasks = getTasks();
		fTableViewer.setInput(project);
		
		//change rows with defaulttasks checked
		if(defaultTasks!=null){
			for(TaskView defTask : defaultTasks){
				fTableViewer.setChecked(defTask, true);
			}			
		}
		fTableViewer.refresh();
	}
	
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		//build tasks string
		StringBuilder sb = new StringBuilder();
		for(TaskView task : selectedTasks){
			sb.append(task.toString()).append(" ");
		}
		
		configuration.setAttribute(IGradleConstants.GRADLE_TASKS_ATTRIBUTES, sb.toString().trim());

	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		//change rows with defaulttasks checked
		for(TaskView defTask : defaultTasks){
			fTableViewer.setChecked(defTask, true);
		}
	}

	
	public void propertyChange(PropertyChangeEvent event) {
		
	}
}
