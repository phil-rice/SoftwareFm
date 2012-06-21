package org.softwarefm.eclipse.link.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.apache.maven.model.Model;
import org.easymock.EasyMock;
import org.softwarefm.eclipse.constants.TemplateConstants;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.mediawiki.Wiki;
import org.softwarefm.utilities.tests.IIntegrationTest;

public class MakeLinkTest extends TestCase implements IIntegrationTest {

	private final static Random random = new Random();
	private final static String version = random.nextInt(100) + "." + random.nextInt(100);
	private final static ArtifactData artifactData = new ArtifactData(new FileAndDigest(new File("someFileName"), "012345"), "someGroupId", "someArtifactId", version);
	private IUrlStrategy urlStrategy;
	private MakeLink makeLink;
	private HostOffsetAndUrl projectUrl;
	private Wiki wiki;
	private ITemplateStore templateStore;
	private IHasCache cache;

	public void testMakesDigestLinkClearsCacheAndCallsPostMakeDigest() {
		cache.clearCaches();
		EasyMock.replay(cache);
		makeLink.makeDigestLink(artifactData);
		checkPageForWikiText(makeLink, "digest:012345", "someGroupId\n" + //
				"someArtifactId\n" + //
				version + "\n" + //
				"\n" + //
				"This page is the link from the file someFileName and the pages:\n" + //
				"* [[artifact:someGroupId/someArtifactId]]\n" + //
				"* [[artifact:someGroupId/someArtifactId/" + version + "]]");
	}

	public void testPopulateProjectWhenBlankWithNoModel() throws Exception {
		deleteProjectInWiki();
		EasyMock.replay(cache);
		makeLink.populateProjectIfBlank(artifactData, null);
		checkPageForWikiTextAndProjectEnd(makeLink, projectUrl.url, "{{Template:Artifact\n" + //
				"|groupId=someGroupId\n" + //
				"|artifactId=someArtifactId\n" + //
				"|projectName=someArtifactId\n" + //
				"|description=\n" + //
				"|projectWebsite=\n" + //
				"|mailingList=\n" + //
				"|issues=\n" + //
				"}}");
	}

	public void testPopulateProjectWhenNotBlankDoesntChangeAnything() {
		EasyMock.replay(cache);
		makeLink.set(projectUrl.url, "test");
		makeLink.populateProjectIfBlank(artifactData, null);
		checkPageForWikiText(makeLink, projectUrl.url, "test\n");
	}

	public void testPopulateWhenBlankWithModelWithLittleData() throws Exception {
		EasyMock.replay(cache);
		deleteProjectInWiki();

		IMaven maven = IMaven.Utils.makeImport();
		URL resource = getClass().getResource("pomWithLittleData.xml");
		Model model = maven.pomToModel(resource.toURI().toString());
		makeLink.populateProjectIfBlank(artifactData, model);
		checkPageForWikiTextAndProjectEnd(makeLink, projectUrl.url, "{{Template:Artifact\n" + //
				"|groupId=someGroupId\n" + //
				"|artifactId=someArtifactId\n" + //
				"|projectName=someArtifactId\n" + //
				"|description=\n" + //
				"|projectWebsite=\n" + //
				"|mailingList=\n" + //
				"|issues=\n" + //
				"}}");
	}

	public void testPopulateWhenBlankWithModelWithData() throws Exception {
		EasyMock.replay(cache);
		deleteProjectInWiki();

		IMaven maven = IMaven.Utils.makeImport();
		URL resource = getClass().getResource("populatedPom.xml");
		Model model = maven.pomToModel(resource.toURI().toString());
		makeLink.populateProjectIfBlank(artifactData, model);
		checkPageForWikiTextAndProjectEnd(makeLink, projectUrl.url, "{{Template:Artifact\n" + //
				"|groupId=someGroupId\n" + //
				"|artifactId=someArtifactId\n" + //
				"|projectName=someName\n" + //
				"|description=someDescription\n" + //
				"|projectWebsite=someUrl\n" + //
				"|mailingList=\n" + //
				"|issues=someIssueUrl\n" + //
				"}}");
	}

	private void deleteProjectInWiki() throws IOException, LoginException {
		makeLink.login();
		wiki.delete(projectUrl.url, "test");
	}

	private void checkPageForWikiText(IMakeLink makeLink, String title, String expected) {
		try {
			String actual = ((MakeLink) makeLink).getWiki().getPageText(title);
			assertEquals(expected.trim(), actual.trim());
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}

	}

	private void checkPageForWikiTextAndProjectEnd(IMakeLink makeLink, String title, String expected) {
		String projectEndTemplate = templateStore.getTemplate(TemplateConstants.artifactEndTemplate);
		checkPageForWikiText(makeLink, title, expected + "\n\n" + projectEndTemplate);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		urlStrategy = IUrlStrategy.Utils.urlStrategy();
		cache = EasyMock.createMock(IHasCache.class);
		makeLink = (MakeLink) IMakeLink.Utils.makeLink(urlStrategy, cache);
		projectUrl = urlStrategy.projectUrl(artifactData);
		wiki = makeLink.getWiki();
		templateStore = makeLink.templateStore;

	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(cache);
		super.tearDown();

	}
}
