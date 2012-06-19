package org.softwarefm.eclipse.url.internal;

import junit.framework.TestCase;

import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;

public class UrlStrategyWithActionBarStateTest extends TestCase {

	private SfmActionState state;
	private IUrlStrategy urlStrategy;

	public void testClassAndMethodUrl() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "code:package/class"), urlStrategy.classAndMethodUrl(new CodeData("package", "class")));
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "code:package/class/meth"), urlStrategy.classAndMethodUrl(new CodeData("package", "class", "meth")));

		state.setUrlSuffix("someSuffix");

		assertEquals(new HostOffsetAndUrl("someHost", "offset", "code:package/class"), urlStrategy.classAndMethodUrl(new CodeData("package", "class")));
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "code:package/class/meth"), urlStrategy.classAndMethodUrl(new CodeData("package", "class", "meth")));
	}

	public void testDigest() {
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
