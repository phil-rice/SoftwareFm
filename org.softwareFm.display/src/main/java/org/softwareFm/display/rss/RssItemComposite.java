package org.softwareFm.display.rss;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.jdom.Element;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.utilities.strings.Strings;

public class RssItemComposite implements IHasControl {

	private final Group content;

	public RssItemComposite(Composite parent, int style, Element element) {
		this.content = new Group(parent, style);
		String title = getValue(element, "title");
		String description = Strings.oneLineLowWhiteSpace(getValue(element, "description"));
		this.content.setText(title);
		new StyledText(content, SWT.MULTI | SWT.WRAP | SWT.FULL_SELECTION).setText(description);
		Grid.addGrabHorizontalAndFillGridDataToAllChildren(content);

	}

	private String getValue(Element element, String name) {
		Element childElement = element.getChild(name);
		return childElement == null ? "" : childElement.getValue();
	}

	@Override
	public Control getControl() {
		return content;
	}

}
