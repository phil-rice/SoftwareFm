package org.softwarefm.display.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TitleAndText extends AbstractTitleAnd {

	private final Text text;

	public TitleAndText(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		super(config, parent, titleOrTitleKey, titleIsKey);
		text = new Text(getComposite(), SWT.NULL);
		text.setLayoutData(new RowData(config.layout.valueWidth, config.layout.textHeight));
	}

	public void setText(String text) {
		this.text.setText(text);
	}



	public String getText() {
		return text.getText();
	}
	

}
