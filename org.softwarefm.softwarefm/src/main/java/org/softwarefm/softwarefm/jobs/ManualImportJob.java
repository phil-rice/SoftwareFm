package org.softwarefm.softwarefm.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.resources.IResourceGetter;

public class ManualImportJob implements ICallback2<ProjectData, Integer> {

	private final IMakeLink makeLink;
	private final IResourceGetter resourceGetter;
	private final ISelectedBindingManager<?> selectedBindingManager;

	public ManualImportJob(IMakeLink makeLink, IResourceGetter resourceGetter, ISelectedBindingManager<?> manager) {
		this.makeLink = makeLink;
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = manager;
	}

	@Override
	public void process(final ProjectData projectData, final Integer thisSelectionId) throws Exception {
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
					IWorkbench workbench = PlatformUI.getWorkbench();
					workbench.getDisplay().asyncExec(new Runnable() {
						public void run() {
							try {
								selectedBindingManager.reselect(thisSelectionId);
							} catch (Exception e) {
								e.printStackTrace();
								throw WrappedException.wrap(e);
							}
						}
					});

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
