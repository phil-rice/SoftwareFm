package org.softwarefm.softwarefm.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.resources.IResourceGetter;

public class ManualImportJob implements ICallback<ProjectData> {

	private final IMakeLink makeLink;
	private final IResourceGetter resourceGetter;

	public ManualImportJob(IMakeLink makeLink, IResourceGetter resourceGetter) {
		this.makeLink = makeLink;
		this.resourceGetter = resourceGetter;
	}

	@Override
	public void process(final ProjectData projectData) throws Exception {
		final String name = IResourceGetter.Utils.getMessageOrException(resourceGetter, TextKeys.jobManualImportTitle, projectData.groupId, projectData.artifactId, projectData.version);
		Job job = new Job(name) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask(name, 2);
					monitor.subTask("Creating Link");
					makeLink.makeDigestLink(projectData);
					monitor.internalWorked(1);
					
					monitor.subTask("Populating project if needed");
					makeLink.populateProjectIfBlank(projectData, null);
					monitor.internalWorked(1);
					return Status.OK_STATUS;
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		};
		job.setUser(true);
		job.schedule();
	}
}
