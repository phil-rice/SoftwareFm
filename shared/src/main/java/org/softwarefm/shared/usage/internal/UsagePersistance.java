package org.softwarefm.shared.usage.internal;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.exceptions.WrappedException;

public class UsagePersistance implements IUsagePersistance {

	private final static IUsageStats empty = IUsageStats.Utils.from();

	public IUsageStats parse(String text) {
		if (text != null && text.length() > 0) {
			Element rootNode = getRootElementFromText(text);
			return nodeToUsage(rootNode);
		}
		return empty;
	}

	@SuppressWarnings("unchecked")
	private IUsageStats nodeToUsage(Element rootNode) {
		Map<String, UsageStatData> result = new HashMap<String, UsageStatData>();
		if (!rootNode.getName().equalsIgnoreCase("Usage"))
			throw new IllegalArgumentException("Expecting Usage Have " + rootNode);
		List<Element> items = new ArrayList<Element>(rootNode.getChildren("Item"));

		for (Element item : items) {
			String path = getAttributeAsString(item, "path");
			int count = getAttributeAsInt(item, "count");
			result.put(path, new UsageStatData(count));
		}
		return IUsageStats.Utils.fromMap(result);
	}

	private Element getRootElementFromText(String text) {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(new StringReader(text));
			Element rootNode = document.getRootElement();
			return rootNode;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public String saveUsageStats(IUsageStats usageStats) {
		Element usageElement = usage(usageStats);
		return elementToString(usageElement);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void populate(ISocialManager manager, String saved) {
		Element root = getRootElementFromText(saved);
		String myName = root.getAttributeValue("name");
		manager.setMyName(myName);
		Element friends = root.getChild("Friends");
		List<FriendData> friendDatas = new ArrayList<FriendData>();
		for (Element friend : (List<Element>) friends.getChildren())
			friendDatas.add(new FriendData(friend.getAttributeValue("name"), friend.getAttributeValue("imageUrl")));
		manager.setFriendsData(friendDatas);
		Element usage = root.getChild("Usage");
		manager.clearUsageData();
		for (Element person : (List<Element>) usage.getChildren()) {
			String name = person.getAttributeValue("name");
			IUsageStats usageStats = nodeToUsage(person);
			manager.setUsageData(name, usageStats);
		}
	}

	@Override
	public String save(ISocialManager manager) {
		Element root = new Element("SocialManager");
		if (manager.myName() != null)
			root.setAttribute("name", manager.myName());
		Element friends = friendData(manager.myFriends());
		root.addContent(friends);
		Element usage = usage(manager);
		root.addContent(usage);
		return elementToString(root);
	}

	private Element usage(ISocialManager manager) {
		Element root = new Element("Usage");
		for (String name : manager.names()) {
			Element usageStats = usage(manager.getUsageStats(name));
			usageStats.setAttribute("name", name);
			root.addContent(usageStats);
		}
		return root;
	}

	private Element friendData(List<FriendData> myFriends) {
		Element root = new Element("Friends");
		for (FriendData data : myFriends) {
			Element child = new Element("Friend");
			child.setAttribute("name", data.name);
			if (data.imageUrl != null)
				child.setAttribute("imageUrl", data.imageUrl);
			root.addContent(child);
		}
		return root;
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public ISimpleMap<String, IUsageStats> parseFriendsUsage(String text) {
	// if (text == null|| text.equals(""))
	// return SimpleMaps.empty();
	// Element rootNode = getRootElementFromText(text);
	// if (!rootNode.getName().equalsIgnoreCase("Friends"))
	// throw new IllegalArgumentException("Expected Friends had " + rootNode);
	// Map<String, IUsageStats> result = new HashMap<String, IUsageStats>();
	// for (Element usage : (List<Element>) rootNode.getChildren()) {
	// IUsageStats usageStats = nodeToUsage(usage);
	// String name = usage.getAttributeValue("name");
	// result.put(name, usageStats);
	// }
	// return SimpleMaps.fromMap(result);
	// }
	//
	// @Override
	// public String saveFriendsUsage(ISimpleMap<String, IUsageStats> friendsUsage) {
	// Element friendsElement = new Element("Friends");
	// friendsElement.setAttribute("version", "1.0");
	// for (String key : friendsUsage.keys()) {
	// Element usage = usage(friendsUsage.get(key));
	// usage.setAttribute("name", key);
	// friendsElement.addContent(usage);
	// }
	//
	// return elementToString(friendsElement);
	// }

	private String elementToString(Element element) {
		try {
			Document doc = new Document(element);
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

	private Element usage(IUsageStats usageStats) {
		Element usageElement = new Element("Usage");

		usageElement.setAttribute("version", "1.0");

		List<String> keys = new ArrayList<String>();
		for (int i = 0; i < usageStats.size(); i++)
			keys.add(usageStats.key(i));
		Collections.sort(keys);
		for (String key : keys) {
			Element item = new Element("Item");
			usageElement.addContent(item);
			item.setAttribute("path", key);
			item.setAttribute("count", Integer.toString(usageStats.get(key).count));
		}
		return usageElement;
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

}
