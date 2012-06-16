package org.softwarefm.eclipse.selection.internal;

import org.jdom.Attribute;
import org.jdom.Element;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.IProjectHtmlRipper;
import org.softwarefm.utilities.jdom.Jdoms;
import org.softwarefm.utilities.strings.Strings;

public class SoftwareFmProjectHtmlRipper implements IProjectHtmlRipper {

	public ProjectData rip(FileAndDigest fileAndDigest, String html) {
		Element element = findMwContentText(html);
		if (element != null) {
			Element p = element.getChild("p");
			String text = p.getText();
			// System.out.println("p text is\n" + text);
			String[] lines = text.split("\n");
			if (lines.length > 1) {
				String groupId = Strings.forUrl(lines[0].trim().toLowerCase());
				String artefactId = Strings.forUrl(lines[1].trim().toLowerCase());
				String version = lines.length > 2 ? Strings.forUrl(lines[2].trim().toLowerCase()) : null;
				return new ProjectData(fileAndDigest, groupId, artefactId, version);
			}
		}

		return null;
	}

	private Element findMwContentText(String from) {
		for (Element div : Jdoms.findElementsWith(from, "div")) {
			Attribute id = div.getAttribute("id");
			if (id != null)
				if ("mw-content-text".equals(id.getValue()))
					return div;
		}
		return null;
	}

}
