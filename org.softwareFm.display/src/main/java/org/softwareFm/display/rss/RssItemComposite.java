package org.softwareFm.display.rss;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jdom.Element;
import org.softwareFm.display.composites.IHasControl;

public class RssItemComposite implements IHasControl{

	private final StyledText content;

	public RssItemComposite(Composite parent, int style, Element element) {
		this. content = new StyledText(parent, style);
		this.content.setText(element.getName());
	}

	@Override
	public Control getControl() {
		return content;
	}

}
