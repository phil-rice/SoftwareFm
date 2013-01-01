package org.softwarefm.utilities.jdom;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.exceptions.WrappedException;

public class Jdoms {

	@SuppressWarnings("unchecked")
	public static List<Element> findElementsWith(final String xml, final String tag) {
		try {
			Reader reader = new StringReader(xml);
			Document document = new SAXBuilder().build(reader);
			List<Element> result = new ArrayList<Element>();
			for (Iterator<Element> descendants = document.getRootElement().getDescendants(new ElementFilter(tag)); descendants.hasNext();)
				result.add(descendants.next());
			return result;
			
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	public static Element findOnlyDivWithId(String xml, String id) {
		List<Element> result = Lists.newList();
		for (Element element : findElementsWith(xml, "div")) {
			if (id.equals(element.getAttributeValue("id")))
				result.add(element);
		}
		return Lists.getOnly(result);
	}

	@SuppressWarnings("unchecked")
	public static Element findOnlyChildTag(Element parent, String tag) {
		Iterator<Element> descendants = parent.getDescendants(new ElementFilter(tag));
		if (descendants.hasNext()) {
			Element result = descendants.next();
			if (descendants.hasNext())
				throw new IllegalStateException("Cannot work out only '" + tag + "' descendant of\n" + parent + "\nas it has multiple");
			return result;
		}
		throw new IllegalStateException("Cannot work out only '" + tag + "' descendant of\n" + parent + "\nas it has none");
	}

}
