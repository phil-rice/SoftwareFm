package org.softwarefm.utilities.jdom;

import java.io.Reader;
import java.io.StringReader;
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
	public static Iterable<Element> findElementsWith(final String xml, final String tag) {
		return new Iterable<Element>() {
			public Iterator<Element> iterator() {
				try {
					Reader reader = new StringReader(xml);
					Document document = new SAXBuilder().build(reader);
					return  document.getRootElement().getDescendants(new ElementFilter(tag));
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		};
	}
	
	public static Element findOnlyDivWithId(String xml, String id){
		List<Element> result = Lists.newList();
		for (Element element: findElementsWith(xml, "div")){
			if (id.equals(element.getAttributeValue("id")))
					result.add(element);
		}
		return Lists.getOnly(result);
	}
	

}
