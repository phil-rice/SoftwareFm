package org.softwarefm.softwarefm.jobs;

import java.io.File;

import org.apache.maven.model.Model;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class MavenImportJob implements ICallback2<String, Integer> {

	private final IMakeLink makeLink;
	private final IMaven maven;
	private final IResourceGetter resourceGetter;
	private final ISelectedBindingManager<?> selectedBindingManager;

	public MavenImportJob(IMaven maven, IMakeLink makeLink, IResourceGetter resourceGetter, ISelectedBindingManager<?> selectedBindingManager) {
		this.maven = maven;
		this.makeLink = makeLink;
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
	}

	public void process(final String pomUrl, final Integer thisSelectionId) throws Exception {
		Job job = new Job(IResourceGetter.Utils.getMessageOrException(resourceGetter, TextKeys.jobMavenImportTitle, pomUrl)) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("MavenImport: " + Strings.lastSegment(pomUrl, "/"), 5);

					monitor.subTask("Download POM");
					Model model = maven.pomToModel(pomUrl);
					File jarFile = maven.jarFile(model);
					monitor.internalWorked(1);

					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
					monitor.subTask("Download Jar to " + jarFile);
					File jar = maven.downloadJar(model);
					monitor.internalWorked(1);

					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;

					monitor.subTask("Digesting");
					String digest = Files.digestAsHexString(jar);
					monitor.internalWorked(1);

					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;

					monitor.subTask("Creating Link");
					FileAndDigest fileNameAndDigest = new FileAndDigest(jar, digest);
					ProjectData projectData = new ProjectData(fileNameAndDigest, IMaven.Utils.getGroupId(model), IMaven.Utils.getArtifactId(model), IMaven.Utils.getVersion(model));
					makeLink.makeDigestLink(projectData);
					monitor.internalWorked(1);

					monitor.subTask("Populating project if needed");
					makeLink.populateProjectIfBlank(projectData, model);
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
