package org.softwarefm.eclipse.url.internal;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.url.HostAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.strings.Strings;

public class UrlStrategy implements IUrlStrategy {

	private final String hostname;
	private final String pageOffset;
	private final String apiOffset;

	public UrlStrategy(String hostname, String pageOffset, String apiOffset) {
		this.hostname = hostname;
		this.pageOffset = pageOffset;
		this.apiOffset = apiOffset;
	}

	public HostAndUrl classAndMethodUrl(ExpressionData expressionData) {
		String packageAndClass = Strings.url(expressionData.packageName, expressionData.className);
		String rl = expressionData.methodName == null ? packageAndClass : Strings.url(packageAndClass, expressionData.methodName);
		return new HostAndUrl(hostname, pageOffset, "java", rl);
	}

	public HostAndUrl digestUrl(String digest) {
		return new HostAndUrl(hostname, pageOffset, "digest", digest);
	}

	public HostAndUrl projectUrl(ProjectData projectData) {
		return new HostAndUrl(hostname, pageOffset, "project", projectData.groupId, projectData.artifactId);
	}

	public HostAndUrl versionUrl(ProjectData projectData) {
		return new HostAndUrl(hostname, pageOffset, "project", projectData.groupId, projectData.artifactId, projectData.version);
	}

	public String host() {
		return hostname;
	}

	public String pageOffset() {
		return pageOffset;
	}

	public String apiOffset() {
		return apiOffset;
	}

}
