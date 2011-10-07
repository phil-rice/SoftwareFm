package org.softwareFm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.CompositeConfig;

public abstract class AbstractCompressedText<T extends Control> extends AbstractCompressed<T> {
	protected String rawText ="";
	protected String title ="";

	public AbstractCompressedText(Composite parent, int style, CompositeConfig config) {
		super(parent, style, config);
	}

	abstract protected void updateText();

	public void setText(String rawText) {
		this.rawText = rawText;
		updateText();
	}

	public void setTitle(String title) {
		this.title = title;
		updateText();
	}

	public String getText() {
		return rawText;
	}


}
