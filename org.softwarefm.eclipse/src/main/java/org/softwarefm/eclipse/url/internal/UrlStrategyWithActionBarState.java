package org.softwarefm.eclipse.url.internal;

import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
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

	public HostOffsetAndUrl classAndMethodUrl(ExpressionData expressionData) {
		return urlStrategy.classAndMethodUrl(expressionData);
	}

	public HostOffsetAndUrl digestUrl(String digest) {
		return urlStrategy.digestUrl(digest);
	}

	public HostOffsetAndUrl projectUrl(ProjectData projectData) {
		return addSuffix(urlStrategy.projectUrl(projectData), state.getUrlSuffix());
	}

	private HostOffsetAndUrl addSuffix(HostOffsetAndUrl raw, String urlSuffix) {
		if (urlSuffix == null)
			return raw;
		return new HostOffsetAndUrl(raw.host, raw.offset, raw.url + "#" + urlSuffix);
	}

	public HostOffsetAndUrl versionUrl(ProjectData projectData) {
		return urlStrategy.versionUrl(projectData);
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
