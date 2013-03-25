package org.softwarefm.eclipse.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

//TODO Better error handling with jobs
public class Jobs {

	public static Job run(String title, final IJobWithSections jobWithSections) {
		final Job job = new Job(title) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					while (jobWithSections.hasMoreWorkToDo()) {
						jobWithSections.doNextSection();
						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;
					}
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.schedule();
		return job;
	}

	public static Job run(String title, final Runnable runnable) {
		final Job job = new Job(title) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					runnable.run();
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.schedule();
		return job;

	}
}
