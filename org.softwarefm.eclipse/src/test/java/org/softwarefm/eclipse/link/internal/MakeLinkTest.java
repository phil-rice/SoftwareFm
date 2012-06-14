package org.softwarefm.eclipse.link.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.apache.maven.model.Model;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.mediawiki.Wiki;
import org.softwarefm.utilities.tests.IIntegrationTest;

public class MakeLinkTest extends TestCase implements IIntegrationTest {

	private final static Random random = new Random();
	private final static String version = random.nextInt(100) + "." + random.nextInt(100);
	private final static ProjectData projectData = new ProjectData(new FileNameAndDigest(new File("someFileName"), "012345"), "someGroupId", "someArtifactId", version);
	private IUrlStrategy urlStrategy;
	private MakeLink makeLink;
	private HostOffsetAndUrl projectUrl;
	private Wiki wiki;

	public void testMakesLink() {
		makeLink.makeDigestLink(projectData);
		checkPageForWikiText(makeLink, "digest:012345", "someGroupId\n" + //
				"someArtifactId\n" + //
				version + "\n" + //
				"\n" + //
				"This page is the link from the file someFileName and the pages:\n" + //
				"* [[project/someGroupId/someArtifactId]]\n" + //
				"* [[project/someGroupId/someArtifactId/" + version + "]]\n");
	}

	public void testPopulateProjectWhenBlankWithNoModel() throws Exception {
		deleteProjectInWiki();
		makeLink.populateProjectIfBlank(projectData, null);
		checkPageForWikiText(makeLink, projectUrl.url, "{{Template:Project\n" + //
				"|groupId=someGroupId\n" + //
				"|artifactId=someArtifactId\n" + //
				"|projectName=someArtifactId\n" + //
				"|description=\n" + //
				"|url=\n" + //
				"|issues=\n" + //
				"}}\n");
	}

	public void testPopulateProjectWhenNotBlankDoesntChangeAnything() {
		makeLink.set(projectUrl.url, "test");
		makeLink.populateProjectIfBlank(projectData, null);
		checkPageForWikiText(makeLink, projectUrl.url, "test\n");
	}

	public void testPopulateWhenBlankWithModelWithLittleData() throws Exception {
		deleteProjectInWiki();

		IMaven maven = IMaven.Utils.makeImport();
		URL resource = getClass().getResource("pomWithLittleData.xml");
		Model model = maven.pomToModel(resource.toURI().toString());
		makeLink.populateProjectIfBlank(projectData, model);
		checkPageForWikiText(makeLink, projectUrl.url, "{{Template:Project\n" + //
				"|groupId=someGroupId\n" + //
				"|artifactId=someArtifactId\n" + //
				"|projectName=someArtifactId\n" + //
				"|description=\n" + //
				"|url=\n" + //
				"|issues=\n" + //
				"}}\n");
	}

	public void testPopulateWhenBlankWithModelWithData() throws Exception {
		deleteProjectInWiki();

		IMaven maven = IMaven.Utils.makeImport();
		URL resource = getClass().getResource("populatedPom.xml");
		Model model = maven.pomToModel(resource.toURI().toString());
		makeLink.populateProjectIfBlank(projectData, model);
		checkPageForWikiText(makeLink, projectUrl.url, "{{Template:Project\n" + //
				"|groupId=someGroupId\n" + //
				"|artifactId=someArtifactId\n" + //
				"|projectName=someName\n" + //
				"|description=someDescription\n" + //
				"|url=someUrl\n" + //
				"|issues=someIssueUrl\n" + //
				"}}\n");
	}

	private void deleteProjectInWiki() throws IOException, LoginException {
		makeLink.login();
		wiki.delete(projectUrl.url, "test");
	}

	private void checkPageForWikiText(IMakeLink makeLink, String title, String expected) {
		try {
			assertEquals(expected, ((MakeLink) makeLink).getWiki().getPageText(title));
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		urlStrategy = IUrlStrategy.Utils.urlStrategy();
		makeLink = (MakeLink) IMakeLink.Utils.makeLink(urlStrategy);
		projectUrl = urlStrategy.projectUrl(projectData);
		wiki = makeLink.getWiki();

	}
}
