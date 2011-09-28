package org.softwareFm.display.rss;

import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.ISituationDisplayer;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class RssDisplay implements ISituationDisplayer {

	private final Composite content;
	private final File root;
	private final Composite main;
	private final Label label;
	private final IResourceGetter resourceGetter;

	public RssDisplay(Composite parent, int style, File root, IResourceGetter resourceGetter) {
		this.root = root;
		this.resourceGetter = resourceGetter;
		this.main = new Composite(parent, style) {
			@Override
			public String toString() {
				return "rssDisplay.main: " + super.toString();
			};
		};
		label = new Label(main, SWT.NULL);
		this.content = new Composite(main, SWT.NULL) {
			@Override
			public String toString() {
				return "rssDisplay.content: " + super.toString();
			};

		};
		content.setLayoutData(Swts.makeGrabHorizonalAndFillGridData());
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(main);

	}

	@Override
	public Control getControl() {
		return main;
	}

	@Override
	public void itemSelected(String item) {
		Swts.removeAllChildren(content);
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new FileReader(new File(root, item)));
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format(DisplayConstants.cannotParseRssFeed, item), e);
		}
		Element root = doc.getRootElement();
		Element channel = root.getChild("channel");
		populateTopRow(channel);
		if (channel != null) {
			@SuppressWarnings("unchecked")
			List<Element> items = channel.getChildren("item");
			for (Element itemElement : items)
				new RssItemComposite(content, SWT.BORDER, itemElement);
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
		content.getParent().layout();
		content.getParent().redraw();
		content.layout();
		content.redraw();
		System.out.println("Computed: " + content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void populateTopRow(Element channel) {
		String text = getValue(channel, "title");
		label.setText(MessageFormat.format(IResourceGetter.Utils.getOrException(resourceGetter, DisplayConstants.rssTitleKey), text));
	}

	private String getValue(Element channel, String name) {
		if (channel != null) {
			Element child = channel.getChild(name);
			if (child != null)
				return child.getText();
		}
		return "";
	}


}
