package org.softwarefm.eclipse.url.internal;

import junit.framework.TestCase;

import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;

public class UrlStrategyWithActionBarStateTest extends TestCase {

	private SfmActionState state;
	private IUrlStrategy urlStrategy;

	public void testClassAndMethodUrl() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "java/package/class"), urlStrategy.classAndMethodUrl(new ExpressionData("package", "class")));
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "java/package/class/meth"), urlStrategy.classAndMethodUrl(new ExpressionData("package", "class", "meth")));

		state.setUrlSuffix("someSuffix");

		assertEquals(new HostOffsetAndUrl("someHost", "offset", "java/package/class"), urlStrategy.classAndMethodUrl(new ExpressionData("package", "class")));
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "java/package/class/meth"), urlStrategy.classAndMethodUrl(new ExpressionData("package", "class", "meth")));
	}

	public void testDigest() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "Digest:someDigest"), urlStrategy.digestUrl("someDigest"));
		state.setUrlSuffix("someSuffix");
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "Digest:someDigest"), urlStrategy.digestUrl("someDigest"));
	}

	public void testProjectData() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "project/group/art"), urlStrategy.projectUrl(new ProjectData(null, "group", "art", "ver")));
		state.setUrlSuffix("someSuffix");
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "project/group/art#someSuffix"), urlStrategy.projectUrl(new ProjectData(null, "group", "art", "ver")));
		state.setUrlSuffix(null);
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "project/group/art"), urlStrategy.projectUrl(new ProjectData(null, "group", "art", "ver")));
	}

	public void testVersionData() {
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "project/group/art/ver"), urlStrategy.versionUrl(new ProjectData(null, "group", "art", "ver")));
		state.setUrlSuffix("someSuffix");
		assertEquals(new HostOffsetAndUrl("someHost", "offset", "project/group/art/ver"), urlStrategy.versionUrl(new ProjectData(null, "group", "art", "ver")));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		state = new SfmActionState();
		IUrlStrategy rawUrlStrategy = IUrlStrategy.Utils.urlStrategy("someHost", "offset");
		urlStrategy = IUrlStrategy.Utils.withActionBarState(rawUrlStrategy, state);
	}
}
