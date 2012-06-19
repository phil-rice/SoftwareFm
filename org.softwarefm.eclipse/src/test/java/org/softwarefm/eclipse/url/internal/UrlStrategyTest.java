package org.softwarefm.eclipse.url.internal;

import junit.framework.TestCase;

import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;

public class UrlStrategyTest extends TestCase {
	private final static UrlStrategy urlStrategy = (UrlStrategy) IUrlStrategy.Utils.urlStrategy("someHost", "offset");

	public void testClassAndMethodUrl() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset","code:package/class"), urlStrategy.classAndMethodUrl(new CodeData("package", "class")));
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "code:package/class/meth"), urlStrategy.classAndMethodUrl(new CodeData("package", "class", "meth")));
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
