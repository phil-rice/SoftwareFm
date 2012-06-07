package org.softwarefm.eclipse.link.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.mediawiki.Wiki;

public class MakeLink implements IMakeLink {

	public void makeLink(ProjectData projectData) {
		try {
			Wiki wiki = new Wiki("localhost", "/mediawiki");
			wiki.setUsingCompressedRequests(false);
			wiki.login("Bot", "botme".toCharArray());
			String entity = projectData.groupId + "\n" + projectData.artefactId + "\n" + projectData.version;
			wiki.edit("Digest/012345", entity, "");
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void main(String[] args) throws Exception {
		FileNameAndDigest fileNameAndDigest = new FileNameAndDigest("filename", "012345");
		String now = new SimpleDateFormat().format(new Date());
		ProjectData data = new ProjectData(fileNameAndDigest, now, "someArtifact", "someVersion");
		new MakeLink().makeLink(data);
	}

}
