package org.softwarefm.eclipse.jobs;

import java.io.File;

import org.apache.maven.model.Model;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class MavenImportJob implements ICallback<String> {

	private final SoftwareFmContainer<?> container;
	private final IMakeLink makeLink;
	private final IMaven maven;

	public MavenImportJob(SoftwareFmContainer<?> container, IMaven maven, IMakeLink makeLink) {
		this.container = container;
		this.maven = maven;
		this.makeLink = makeLink;
	}

	public void process(final String pomUrl) throws Exception {
		Job job = new Job(IResourceGetter.Utils.getMessageOrException(container.resourceGetter, SwtConstants.mavenImportKey, pomUrl)) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("MavenImport: " + Strings.lastSegment(pomUrl, "/"), 4);
					
					monitor.subTask("Download POM");
					Model model = maven.pomToModel(pomUrl);
					File jarFile = maven.jarFile(model);
					monitor.internalWorked(1);
					
					monitor.subTask("Download Jar to " + jarFile);
					File jar = maven.downloadJar(model);
					monitor.internalWorked(1);

					monitor.subTask("Digesting");
					String digest = Files.digestAsHexString(jar);
					monitor.internalWorked(1);
					
					monitor.subTask("Storing results in SoftwareFM");
					FileNameAndDigest fileNameAndDigest = new FileNameAndDigest(jar.getCanonicalPath(), digest);
					ProjectData projectData = new ProjectData(fileNameAndDigest, IMaven.Utils.getGroupId(model), IMaven.Utils.getArtifactId(model), IMaven.Utils.getVersion(model));
					makeLink.makeLink(projectData);
					monitor.internalWorked(1);
					return Status.OK_STATUS;
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		};
		job.schedule();
	}
}
