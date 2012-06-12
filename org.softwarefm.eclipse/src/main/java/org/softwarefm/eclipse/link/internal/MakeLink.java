package org.softwarefm.eclipse.link.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.mediawiki.Wiki;
import org.softwarefm.utilities.strings.Strings;

public class MakeLink implements IMakeLink {

	private final IUrlStrategy urlStrategy;

	public MakeLink(IUrlStrategy urlStrategy) {
		this.urlStrategy = urlStrategy;
	}
	
	public void makeLink(ProjectData projectData) {
		try {
			Wiki wiki = new Wiki(urlStrategy.host(), Strings.urlWithSlash(urlStrategy.apiOffset()));
			wiki.setUsingCompressedRequests(false);
			wiki.login("Bot", "botme".toCharArray());
			String entity = projectData.groupId + "\n" + projectData.artifactId + "\n" + projectData.version;
			String digest = projectData.fileNameAndDigest.digest;
			if (digest == null)
				throw new NullPointerException("Digest was null");
			System.out.println("Making link: "+ projectData);
			wiki.edit("Digest/" + digest, entity, "");
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void main(String[] args) throws Exception {
		FileNameAndDigest fileNameAndDigest = new FileNameAndDigest("filename", "012345");
		String now = new SimpleDateFormat().format(new Date());
		ProjectData data = new ProjectData(fileNameAndDigest, now, "someArtifact", "someVersion");
		new MakeLink(IUrlStrategy.Utils.urlStrategy()).makeLink(data);
	}

}
