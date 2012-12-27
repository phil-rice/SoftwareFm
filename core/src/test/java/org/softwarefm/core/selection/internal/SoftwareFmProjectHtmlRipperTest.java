package org.softwarefm.core.selection.internal;

import java.io.File;
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;

public class SoftwareFmProjectHtmlRipperTest extends TestCase {
	private final SoftwareFmArtifactHtmlRipper ripper = new SoftwareFmArtifactHtmlRipper();

	private final String gaPattern = "<div id=''mw-content-text''><p>{0}\n{1}</p></div>";
	private final String gavPattern = "<div id=''mw-content-text''><p>{0}\n{1}\n{2}</p></div>";

	private final FileAndDigest fileAndDigest = new FileAndDigest(new File("file"), "digest");

	public void testLooksForMwContentTextDiv() {
		checkReads("g", "a", "v", "g", "a", "v");
		checkReads("g", "a", "v", " g ", " a ", " v ");
		checkReads("groupid", "artifactid", "version", " group id  ", " artifact id ", " ver sion  ");
		checkReads("g", "a", "v", " G ", " A ", " V ");
		checkReads("groupid", "artifactid", "version", " GRoup Id  ", " artiFact iD ", " ver Sion  ");
		checkReads("g-_roupid", "arti.factid", "version", " G-_Ro!\"�$%$^$up I\'d  ", " arti{}:@~;#.,Fact iD ", " ver Sion  ");
	}

	private void checkReads(String groupId, String artifactId, String version, String rawGroupId, String rawArtifactId, String rawVersion) {
		ArtifactData artifactData = new ArtifactData(fileAndDigest, groupId, artifactId, version);
		ArtifactData projectDataNoVersion = new ArtifactData(fileAndDigest, groupId, artifactId, null);
		String gavDiv = MessageFormat.format(gavPattern, rawGroupId, rawArtifactId, rawVersion);
		String gaDiv = MessageFormat.format(gaPattern, rawGroupId, rawArtifactId);

		assertEquals(artifactData, ripper.rip(fileAndDigest, "<root>" + gavDiv + "</root>"));
		assertEquals(artifactData, ripper.rip(fileAndDigest, "<root><div>" + gavDiv + "</div></root>"));
		assertEquals(artifactData, ripper.rip(fileAndDigest, "<root><div id='someid'>" + gavDiv + "</div></root>"));
		assertEquals(artifactData, ripper.rip(fileAndDigest, "<root><div id='someId'><div>" + gavDiv + "</div></div></root>"));

		assertEquals(projectDataNoVersion, ripper.rip(fileAndDigest, "<root>" + gaDiv + "</root>"));
		assertEquals(projectDataNoVersion, ripper.rip(fileAndDigest, "<root><div>" + gaDiv + "</div></root>"));
		assertEquals(projectDataNoVersion, ripper.rip(fileAndDigest, "<root><div id='someid'>" + gaDiv + "</div></root>"));
		assertEquals(projectDataNoVersion, ripper.rip(fileAndDigest, "<root><div id='someId'><div>" + gaDiv + "</div></div></root>"));
	}

}
