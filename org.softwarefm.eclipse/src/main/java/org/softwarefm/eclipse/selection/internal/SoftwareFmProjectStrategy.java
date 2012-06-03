package org.softwarefm.eclipse.selection.internal;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.IProjectHtmlRipper;
import org.softwarefm.eclipse.selection.IProjectStrategy;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;

public class SoftwareFmProjectStrategy implements IProjectStrategy {
	private final IHttpClient client;
	private final IProjectHtmlRipper htmlRipper;

	public SoftwareFmProjectStrategy(IHttpClient client, String host, IProjectHtmlRipper htmlRipper) {
		this.htmlRipper = htmlRipper;
		this.client = client.host(host);

	}

	public ProjectData findProject(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		try {
			IResponse response = client.get(CommonConstants.softwareFmUrlPrefix + "digest/" + fileNameAndDigest.digest).execute();
			if (CommonConstants.okStatusCodes.contains(response.statusCode())) {
				String html = response.asString();
				ProjectData projectData = htmlRipper.rip(fileNameAndDigest, html);
				return projectData;
			}

			return null;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
