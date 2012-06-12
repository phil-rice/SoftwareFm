package org.softwarefm.softwarefm.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.resources.IResourceGetter;

public class ManualImportJob implements ICallback<ProjectData> {

	private final IMakeLink makeLink;
	private final IMaven maven;
	private final IResourceGetter resourceGetter;

	public ManualImportJob(IMaven maven, IMakeLink makeLink, IResourceGetter resourceGetter) {
		this.maven = maven;
		this.makeLink = makeLink;
		this.resourceGetter = resourceGetter;
	}

	@Override
	public void process(final ProjectData projectData) throws Exception {
		final String name = IResourceGetter.Utils.getMessageOrException(resourceGetter, SwtConstants.manualImportKey, projectData.groupId, projectData.artifactId, projectData.version);
		Job job = new Job(name) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask(name, 1);
					makeLink.makeLink(projectData);
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
