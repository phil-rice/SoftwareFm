/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.constants;

import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.UrlGenerator;

public class SoftwareFmConstants {
	public static final String urlPrefix = "softwareFm";

	public static final String softwareFmServerUrl = "server1.softwarefm.com";
	public static final String gitProtocolAndGitServerName = "git://git.softwarefm.com:80/";

	public static final String groupIdKey = "groupId";
	public static final String artifactIdKey = "artifactId";

	public static final String usagePrefix = "command/Usage";

	public static final String digest = "digest";
	public static final String artifactId = "artifactId";
	public static final String groupId = "groupId";
	public static final String version = "version";

	public static final String snipperFeedType = "feedType.snippet";
	public static final String snipperTemplateKey = "snippet.template";
	public static final String projectCryptoKey = "projectCrypto";
	public static final String projectDirectoryName = "project";
	public static final long usageRefreshTimeMs = 10 * 60 * 60 * 1000;// 10 hours

	public static IUrlGenerator jarUrlGenerator(String prefix) {
		return new UrlGenerator(prefix + "/jars/{0}/{1}/{2}", SoftwareFmConstants.digest);
	}

}