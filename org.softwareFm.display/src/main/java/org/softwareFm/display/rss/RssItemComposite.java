/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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