package org.softwarefm.eclipse.selection.internal;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.IProjectHtmlRipper;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.strings.Strings;

public class SoftwareFmProjectHtmlRipper implements IProjectHtmlRipper {

	@Override
	public ProjectData rip(FileNameAndDigest fileNameAndDigest, String html) {
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
				return new ProjectData(fileNameAndDigest, groupId, artefactId, version);
			}
		}

		return null;
	}

	private Element findMwContentText(String from) {
		try {
			Reader reader = new StringReader(from);
			Document document = new SAXBuilder().build(reader);
			for (@SuppressWarnings("unchecked")
			Iterator<Element> divs = document.getRootElement().getDescendants(new ElementFilter("div")); divs.hasNext();) {
				Element div = divs.next();
				Attribute id = div.getAttribute("id");
				if (id != null)
					if ("mw-content-text".equals(id.getValue()))
						return div;
			}
			return null;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
