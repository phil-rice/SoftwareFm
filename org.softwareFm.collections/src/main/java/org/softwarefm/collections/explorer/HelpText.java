package org.softwarefm.collections.explorer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class HelpText implements IHelpText {

	private final StyledText content;

	public HelpText(Composite parent) {
		content = new StyledText(parent, SWT.WRAP);
		content.setBackground(content.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public void setText(String text) {
		content.setText(text);
	}

}
