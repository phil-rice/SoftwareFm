package org.softwarefm.eclipse.url.internal;

import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
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

	public HostOffsetAndUrl classAndMethodUrl(CodeData codeData) {
		String rl = codeData.methodName == null //
		? Strings.url("code:" + codeData.packageName, codeData.className)
				: Strings.url("code:" + codeData.packageName, codeData.className, codeData.methodName);
		return new HostOffsetAndUrl(hostname, pageOffset, rl);
	}

	public HostOffsetAndUrl digestUrl(String digest) {
		return new HostOffsetAndUrl(hostname, pageOffset, "Digest:" + digest);
	}

	public HostOffsetAndUrl projectUrl(ArtifactData artifactData) {
		return new HostOffsetAndUrl(hostname, pageOffset, "artifact:" + artifactData.groupId, artifactData.artifactId);
	}

	public HostOffsetAndUrl versionUrl(ArtifactData artifactData) {
		return new HostOffsetAndUrl(hostname, pageOffset, "artifact:" + artifactData.groupId, artifactData.artifactId, artifactData.version);
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
