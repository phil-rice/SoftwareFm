package org.softwarefm.eclipse.usage.internal;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.eclipse.usage.UsageStats;
import org.softwarefm.utilities.exceptions.WrappedException;

public class UsagePersistance implements IUsagePersistance {

	@SuppressWarnings("unchecked")
	public IUsage load(String text) {
		Usage usage = new Usage();
		if (text != null && text.length() > 0)
			try {
				SAXBuilder builder = new SAXBuilder();
				Document document = builder.build(new StringReader(text));
				Element rootNode = document.getRootElement();
				List<Element> items = new ArrayList<Element>(rootNode.getChildren("Item"));
			
				for (Element item : items) {
					String path = getAttributeAsString(item, "path");
					int count = getAttributeAsInt(item, "count");
					usage.setUsageStat(path, new UsageStats(count));
				}
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		return usage;
	}

	private String getAttributeAsString(Element item, String key) {
		Attribute attribute = getAttribute(item, key);
		return attribute.getValue();
	}

	private int getAttributeAsInt(Element item, String key) {
		try {
			Attribute attribute = getAttribute(item, key);
			return attribute.getIntValue();
		} catch (DataConversionException e) {
			throw WrappedException.wrap(e);
		}
	}

	private Attribute getAttribute(Element item, String key) {
		Attribute attribute = item.getAttribute(key);
		if (attribute == null)
			throw new IllegalStateException("Cannot find attribute with key " + key + " in " + item);
		return attribute;
	}

	public String save(IUsage usage) {
		try {
			Map<String, UsageStats> stats = usage.getStats();
			Element usageElement = new Element("Usage");
			Document doc = new Document(usageElement);
			doc.setRootElement(usageElement);
			usageElement.setAttribute("version", "1.0");
			List<String> keys = new ArrayList<String>(stats.keySet());
			Collections.sort(keys);
			for (String key: keys) {
				Element item = new Element("Item");
				usageElement.addContent(item);
				item.setAttribute("path", key);
				item.setAttribute("count", Integer.toString(stats.get(key).count));
			}
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			StringWriter writer = new StringWriter();
			xmlOutput.output(doc, writer);
			String text = writer.toString();
			return text;
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}
}
