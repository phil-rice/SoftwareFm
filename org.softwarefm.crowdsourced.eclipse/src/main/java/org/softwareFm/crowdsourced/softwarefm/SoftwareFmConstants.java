package org.softwareFm.crowdsourced.softwarefm;

import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.utilities.url.IUrlGenerator;

public class SoftwareFmConstants {
	public static final String softwareFmServerUrl = "www.softwarefm.com";
	public static final String gitProtocolAndGitServerName = "git://git.softwarefm.com:80/";

	public static final String groupIdKey = "groupId";
	public static final String artifactIdKey = "artifactId";

	public static final String usagePrefix = "command/Usage";
	
	public static final String snipperTemplateKey = "snippet.template";
	public static final String projectCryptoKey = "projectCrypto";
	public static final String projectDirectoryName = "project";

	public static IUrlGenerator projectGenerator() {
		return IUrlGenerator.Utils.generator("softwareFm/users/{0}/{1}/{2}/projects", LoginConstants.softwareFmIdKey);
	}

}
