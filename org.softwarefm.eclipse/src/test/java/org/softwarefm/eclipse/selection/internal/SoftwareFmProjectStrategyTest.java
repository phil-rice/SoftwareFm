package org.softwarefm.eclipse.selection.internal;

import java.io.File;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.IArtifactHtmlRipper;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.strings.Strings;

public class SoftwareFmProjectStrategyTest extends TestCase {

	private IArtifactHtmlRipper htmlRipper;
	private IHttpClient rawClient;
	private IHttpClient withHost;
	private IHttpClient withGet1;
	private IHttpClient withGet2;
	private IHttpClient withGet3;

	private final FileAndDigest fileAndDigest = new FileAndDigest(new File("file"), "digest");
	private final ArtifactData artifactData = new ArtifactData(fileAndDigest, "g", "a", "v");
	private final static String digestUrl = Strings.url(CommonConstants.softwareFmPageOffset, "Digest:digest");

	public void testFindProject() {
		EasyMock.expect(rawClient.host("host")).andReturn(withHost);
		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet1);
		EasyMock.expect(withGet1.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileAndDigest, "projectText")).andReturn(artifactData);
		EasyMock.replay(htmlRipper, rawClient, withHost, withGet1, withGet2, withGet3);

		SoftwareFmArtifactStrategy<String> strategy = new SoftwareFmArtifactStrategy<String>(rawClient, htmlRipper, IUrlStrategy.Utils.urlStrategy("host"));
		strategy.findArtifact("selection", fileAndDigest, 1);
	}

	public void testFindProjectReusesWithGet() {
		EasyMock.expect(rawClient.host("host")).andReturn(withHost);

		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet1);
		EasyMock.expect(withGet1.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileAndDigest, "projectText")).andReturn(artifactData);

		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet2);
		EasyMock.expect(withGet2.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileAndDigest, "projectText")).andReturn(artifactData);

		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet3);
		EasyMock.expect(withGet3.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileAndDigest, "projectText")).andReturn(artifactData);

		EasyMock.replay(htmlRipper, rawClient, withHost, withGet1, withGet2, withGet3);

		SoftwareFmArtifactStrategy<String> strategy = new SoftwareFmArtifactStrategy<String>(rawClient, htmlRipper, IUrlStrategy.Utils.urlStrategy("host"));
		strategy.findArtifact("selection", fileAndDigest, 1);
		strategy.findArtifact("selection", fileAndDigest, 1);
		strategy.findArtifact("selection", fileAndDigest, 1);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		htmlRipper = EasyMock.createMock(IArtifactHtmlRipper.class);
		rawClient = EasyMock.createMock(IHttpClient.class);
		withHost = EasyMock.createMock(IHttpClient.class);
		withGet1 = EasyMock.createMock(IHttpClient.class);
		withGet2 = EasyMock.createMock(IHttpClient.class);
		withGet3 = EasyMock.createMock(IHttpClient.class);
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(htmlRipper, rawClient, withHost, withGet1, withGet2, withGet3);
		super.tearDown();
	}

}
