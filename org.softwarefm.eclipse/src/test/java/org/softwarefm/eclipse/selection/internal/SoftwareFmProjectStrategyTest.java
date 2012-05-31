package org.softwarefm.eclipse.selection.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.IProjectHtmlRipper;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;

public class SoftwareFmProjectStrategyTest extends TestCase {

	private IProjectHtmlRipper htmlRipper;
	private IHttpClient rawClient;
	private IHttpClient withHost;
	private IHttpClient withGet1;
	private IHttpClient withGet2;
	private IHttpClient withGet3;

	private final FileNameAndDigest fileNameAndDigest = new FileNameAndDigest("file", "digest");
	private final ProjectData projectData = new ProjectData(fileNameAndDigest, "g", "a", "v");

	public void testFindProject() {
		String digestUrl = "/wiki/digest/digest";
		EasyMock.expect(rawClient.host("host")).andReturn(withHost);
		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet1);
		EasyMock.expect(withGet1.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileNameAndDigest, "projectText")).andReturn(projectData);
		EasyMock.replay(htmlRipper, rawClient, withHost, withGet1, withGet2, withGet3);

		SoftwareFmProjectStrategy strategy = new SoftwareFmProjectStrategy(rawClient, "host", htmlRipper);
		strategy.findProject(fileNameAndDigest, 1);
	}

	public void testFindProjectReusesWithGet() {
		String digestUrl = "/wiki/digest/digest";
		EasyMock.expect(rawClient.host("host")).andReturn(withHost);

		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet1);
		EasyMock.expect(withGet1.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileNameAndDigest, "projectText")).andReturn(projectData);

		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet2);
		EasyMock.expect(withGet2.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileNameAndDigest, "projectText")).andReturn(projectData);

		EasyMock.expect(withHost.get(digestUrl)).andReturn(withGet3);
		EasyMock.expect(withGet3.execute()).andReturn(IResponse.Utils.okText(digestUrl, "projectText"));
		EasyMock.expect(htmlRipper.rip(fileNameAndDigest, "projectText")).andReturn(projectData);

		EasyMock.replay(htmlRipper, rawClient, withHost, withGet1, withGet2, withGet3);

		SoftwareFmProjectStrategy strategy = new SoftwareFmProjectStrategy(rawClient, "host", htmlRipper);
		strategy.findProject(fileNameAndDigest, 1);
		strategy.findProject(fileNameAndDigest, 1);
		strategy.findProject(fileNameAndDigest, 1);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		htmlRipper = EasyMock.createMock(IProjectHtmlRipper.class);
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
