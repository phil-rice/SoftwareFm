package org.softwarefm.eclipse.url.internal;

import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;

public class UrlStrategyWithActionBarState implements IUrlStrategy {

	private final IUrlStrategy urlStrategy;

	public String host() {
		return urlStrategy.host();
	}

	public String pageOffset() {
		return urlStrategy.pageOffset();
	}

	public String apiOffset() {
		return urlStrategy.apiOffset();
	}

	public HostOffsetAndUrl classAndMethodUrl(CodeData codeData) {
		return urlStrategy.classAndMethodUrl(codeData);
	}

	public HostOffsetAndUrl digestUrl(String digest) {
		return urlStrategy.digestUrl(digest);
	}

	public HostOffsetAndUrl projectUrl(ArtifactData artifactData) {
		return addSuffix(urlStrategy.projectUrl(artifactData), state.getUrlSuffix());
	}

	private HostOffsetAndUrl addSuffix(HostOffsetAndUrl raw, String urlSuffix) {
		if (urlSuffix == null)
			return raw;
		return new HostOffsetAndUrl(raw.host, raw.offset, raw.url + "#" + urlSuffix);
	}

	public HostOffsetAndUrl versionUrl(ArtifactData artifactData) {
		return urlStrategy.versionUrl(artifactData);
	}

	public HostOffsetAndUrl templateUrl(String name) {
		return urlStrategy.templateUrl(name);
	}

	private final SfmActionState state;

	public UrlStrategyWithActionBarState(IUrlStrategy urlStrategy, SfmActionState state) {
		this.urlStrategy = urlStrategy;
		this.state = state;
	}

}
