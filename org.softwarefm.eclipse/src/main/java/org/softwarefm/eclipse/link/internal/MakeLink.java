package org.softwarefm.eclipse.link.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.maven.model.Model;
import org.softwarefm.eclipse.constants.TemplateConstants;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.mediawiki.Wiki;
import org.softwarefm.utilities.strings.Strings;

public class MakeLink implements IMakeLink {

	private final IUrlStrategy urlStrategy;
	final ITemplateStore templateStore;
	private final Wiki wiki;
	private boolean loggedIn;
	private final IHasCache cache;

	public MakeLink(IUrlStrategy urlStrategy, ITemplateStore templateStore, IHasCache cache) {
		this.urlStrategy = urlStrategy;
		this.templateStore = templateStore;
		this.cache = cache;
		wiki = new Wiki(urlStrategy.host(), Strings.urlWithSlash(urlStrategy.apiOffset()));
		wiki.setThrottle(0);
		wiki.setUsingCompressedRequests(false);
	}

	public void makeDigestLink(ProjectData projectData) {
		try {
			HostOffsetAndUrl projectUrl = urlStrategy.projectUrl(projectData);
			HostOffsetAndUrl versionUrl = urlStrategy.versionUrl(projectData);
			String digest = projectData.fileAndDigest.digest;
			if (digest == null)
				throw new NullPointerException("Digest was null");
			String template = templateStore.getTemplate(TemplateConstants.digestTemplate);
			String entity = MessageFormat.format(template, projectData.groupId, projectData.artifactId, projectData.version, projectData.fileAndDigest.file.getName(), projectUrl.url, versionUrl.url);
			System.out.println("Entity:\n" + entity);

			HostOffsetAndUrl digestUrl = urlStrategy.digestUrl(digest);
			set(digestUrl.url, entity);
			cache.clearCaches();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	void set(String url, String entity) {
		try {
			if (!loggedIn)
				login();
			wiki.edit(url, entity, "");
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public Wiki getWiki() {
		return wiki;
	}

	void login() {
		try {
			wiki.login("Bot", "botme".toCharArray());// we could optimise this by trying / failing / logging in / trying... or some other strategy
			loggedIn = true;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public void populateProjectIfBlank(ProjectData projectData, Model model) {
		String projectName = model == null ? projectData.artifactId : IMaven.Utils.getName(model);
		String description = Strings.nullSafeToString(model == null ? null : model.getDescription());
		String projectWebsite = Strings.nullSafeToString(model == null ? null : model.getUrl());
		String mailingList = "";
		String issues = Strings.nullSafeToString(model == null ? null : IMaven.Utils.getIssueManagementUrl(model));

		String projectTemplate = templateStore.getTemplate(TemplateConstants.projectTemplate);
		String projectEndTemplate = templateStore.getTemplate(TemplateConstants.projectEndTemplate);
		String entity = MessageFormat.format(projectTemplate, projectData.groupId, projectData.artifactId, projectName, description, projectWebsite, mailingList, issues) + "\n" + projectEndTemplate;

		String url = urlStrategy.projectUrl(projectData).url;
		try {
			wiki.getPageText(url);
		} catch (FileNotFoundException e) {
			set(url, entity);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void main(String[] args) throws Exception {
		FileAndDigest fileNameAndDigest = new FileAndDigest(new File("somePath/File.jar"), "012345");
		ProjectData data = new ProjectData(fileNameAndDigest, "GroupId", "someArtifact", "someVersion");
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy();
		ITemplateStore TemplateStore = ITemplateStore.Utils.templateStore(urlStrategy);
		MakeLink makeLink = new MakeLink(urlStrategy, TemplateStore, new IHasCache() {
			public void clearCaches() {
				System.out.println("Cache cleared");
			}
		});
		makeLink.makeDigestLink(data);
		makeLink.populateProjectIfBlank(data, null);
	}

}
