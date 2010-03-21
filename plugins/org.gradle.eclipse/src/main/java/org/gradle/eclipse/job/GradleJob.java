package org.gradle.eclipse.job;

import org.eclipse.core.runtime.jobs.Job;

abstract class GradleJob extends Job{

	public GradleJob(String name) {
		super(name);
	}


	class BooleanHolder {
		
		private boolean value = false;

		public void setValue(boolean value) {
			this.value = value;
		}

		public boolean getValue() {
			return value;
		}
	}
}
