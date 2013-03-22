package org.softwarefm.core.url.internal;

import junit.framework.TestCase;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.core.url.IUrlStrategy;

public class UrlStrategyTest extends TestCase {
	private final static UrlStrategy urlStrategy = (UrlStrategy) IUrlStrategy.Utils.urlStrategy("someHost", "offset");

	public void testClassAndMethodUrl() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset","code:someSfmId"), urlStrategy.classAndMethodUrl(new CodeData("someSfmId")));
	}

	public void testDigest() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "Digest:someDigest"), urlStrategy.digestUrl("someDigest"));
	}

	public void testProjectData() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "artifact:group/art"), urlStrategy.projectUrl(new ArtifactData(null, "group", "art", "ver")));
	}

	public void testVersionData() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "artifact:group/art/ver"), urlStrategy.versionUrl(new ArtifactData(null, "group", "art", "ver")));
	}

}
