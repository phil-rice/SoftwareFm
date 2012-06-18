package org.softwarefm.eclipse.selection.internal;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.IProjectHtmlRipper;
import org.softwarefm.eclipse.selection.IProjectStrategy;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;

public class SoftwareFmProjectStrategy<S> implements IProjectStrategy<S> {
	private final IHttpClient client;
	private final IProjectHtmlRipper htmlRipper;
	private final IUrlStrategy urlStrategy;

	public SoftwareFmProjectStrategy(IHttpClient client, IProjectHtmlRipper htmlRipper, IUrlStrategy urlStrategy) {
		this.htmlRipper = htmlRipper;
		this.urlStrategy = urlStrategy;
		this.client = client.host(urlStrategy.host());

	}

	public ProjectData findProject(S selection, FileAndDigest fileAndDigest, int selectionCount) {
		try {
			String url = urlStrategy.digestUrl(fileAndDigest.digest).getOffsetAndUrl();
			IResponse response = client.get(url).execute();
			if (CommonConstants.okStatusCodes.contains(response.statusCode())) {
				String html = response.asString();
				try {
					ProjectData projectData = htmlRipper.rip(fileAndDigest, html);
					return projectData;
				} catch (Exception e) {
					//TODO This swallows the exception. Probably a bad idea. Probably should send message to softwarefm and inform the user...quite complex story
				}
			}

			return null;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
