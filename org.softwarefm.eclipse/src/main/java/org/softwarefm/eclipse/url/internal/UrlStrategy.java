package org.softwarefm.eclipse.url.internal;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.strings.Strings;

public class UrlStrategy implements IUrlStrategy {

	private final String hostname;
	private final String pageOffset;
	private final String apiOffset;
	private final String templateOffset;

	public UrlStrategy(String hostname, String pageOffset, String apiOffset, String templateOffset) {
		this.hostname = hostname;
		this.pageOffset = pageOffset;
		this.apiOffset = apiOffset;
		this.templateOffset = templateOffset;
	}

	public HostOffsetAndUrl classAndMethodUrl(ExpressionData expressionData) {
		String packageAndClass = Strings.url(expressionData.packageName, expressionData.className);
		String rl = expressionData.methodName == null ? packageAndClass : Strings.url(packageAndClass, expressionData.methodName);
		return new HostOffsetAndUrl(hostname, pageOffset, "java", rl);
	}

	public HostOffsetAndUrl digestUrl(String digest) {
		return new HostOffsetAndUrl(hostname, pageOffset, "Digest:" + digest);
	}

	public HostOffsetAndUrl projectUrl(ProjectData projectData) {
		return new HostOffsetAndUrl(hostname, pageOffset, "project", projectData.groupId, projectData.artifactId);
	}

	public HostOffsetAndUrl versionUrl(ProjectData projectData) {
		return new HostOffsetAndUrl(hostname, pageOffset, "project", projectData.groupId, projectData.artifactId, projectData.version);
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

	public HostOffsetAndUrl templateUrl(String name) {
		return new HostOffsetAndUrl(hostname, templateOffset, name);
	}

}
