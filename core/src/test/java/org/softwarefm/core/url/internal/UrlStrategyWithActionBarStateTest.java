package org.softwarefm.core.url.internal;

import junit.framework.TestCase;

import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.core.url.IUrlStrategy;

public class UrlStrategyWithActionBarStateTest extends TestCase {

	private SfmActionState state;
	private IUrlStrategy urlStrategy;

	public void testClassAndMethodUrlAreUnaffectedByState() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "code:someSfmId"), urlStrategy.classAndMethodUrl(new CodeData("someSfmId")));

		state.setUrlSuffix("someSuffix");

		assertEquals(new HostOffsetAndUrl("someHost", "offset", "code:someSfmId"), urlStrategy.classAndMethodUrl(new CodeData("someSfmId")));
	}

	public void testDigestIsUnaffectedByState() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "Digest:someDigest"), urlStrategy.digestUrl("someDigest"));
		state.setUrlSuffix("someSuffix");
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "Digest:someDigest"), urlStrategy.digestUrl("someDigest"));
	}

	public void testProjectData() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "artifact:group/art"), urlStrategy.projectUrl(new ArtifactData(null, "group", "art", "ver")));
		state.setUrlSuffix("someSuffix");
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "artifact:group/art#someSuffix"), urlStrategy.projectUrl(new ArtifactData(null, "group", "art", "ver")));
		state.setUrlSuffix(null);
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "artifact:group/art"), urlStrategy.projectUrl(new ArtifactData(null, "group", "art", "ver")));
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		state = new SfmActionState();
		IUrlStrategy rawUrlStrategy = IUrlStrategy.Utils.urlStrategy("someHost", "offset");
		urlStrategy = IUrlStrategy.Utils.withActionBarState(rawUrlStrategy, state);
	}
}
