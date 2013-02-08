package org.softwarefm.core.selection.internal;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.IArtifactHtmlRipper;
import org.softwarefm.core.selection.IArtifactStrategy;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.shared.constants.CommonConstants;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;

public class SoftwareFmArtifactStrategy<S> implements IArtifactStrategy<S> {
	private final IHttpClient client;
	private final IArtifactHtmlRipper htmlRipper;
	private final IUrlStrategy urlStrategy;

	public SoftwareFmArtifactStrategy(IHttpClient client, IArtifactHtmlRipper htmlRipper, IUrlStrategy urlStrategy) {
		this.htmlRipper = htmlRipper;
		this.urlStrategy = urlStrategy;
		this.client = client.host(urlStrategy.host());
	}

	public ArtifactData findArtifact(S selection, FileAndDigest fileAndDigest, int selectionCount) {
		try {
			String url = urlStrategy.digestUrl(fileAndDigest.digest).getOffsetAndUrl();
			IResponse response = client.get(url).execute();
			if (CommonConstants.okStatusCodes.contains(response.statusCode())) {
				String html = response.asString();
				try {
					ArtifactData artifactData = htmlRipper.rip(fileAndDigest, html);
					return artifactData;
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
