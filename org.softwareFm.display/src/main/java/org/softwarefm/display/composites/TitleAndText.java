package org.softwarefm.display.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TitleAndText extends AbstractTitleAnd {

	private Text text;
	private boolean globalEdit;

	public TitleAndText(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		super(config, parent, titleOrTitleKey, titleIsKey);
		text = new Text(getComposite(), SWT.NULL);
		text.setLayoutData(new RowData(SWT.DEFAULT, config.layout.textHeight));
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	public void setGlobalEdit(boolean mutable) {
		this.globalEdit = mutable;
		this.text.setEditable(false);
		updateDisplay();
	}

	private void updateDisplay() {
		if (text.getEditable())
			text.setBackground(config.editingBackground);
		else
			text.setBackground(config.notEditingBackground);
		
	}

}
