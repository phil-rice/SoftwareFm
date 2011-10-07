package org.softwareFm.display.displayer;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public abstract class AbstractCompressedText<T extends Control> implements IDisplayer {
	protected final CompositeConfig config;
	protected final Composite content;
	protected final Composite buttonPanel;
	protected final T text;
	protected String rawText ="";
	protected String title ="";
	protected final Composite buttonAndTitlePanel;

	public AbstractCompressedText(Composite parent, int style, CompositeConfig config) {
		this.config = config;
		this.content = Swts.newComposite(parent, style, getClass().getSimpleName());
		this.buttonAndTitlePanel = makeButtonAndTitlePanel(content);
		this.buttonPanel = Swts.newComposite(buttonAndTitlePanel, style, "ButtonPanel");
		this.text = makeTextControl(content, SWT.NULL);
		setLayout();
	}

	abstract protected Composite makeButtonAndTitlePanel(Composite parent);

	abstract protected void setLayout();

	abstract protected T makeTextControl(Composite parent, int style);

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

	@Override
	public void buttonAdded(IHasControl button) {
		button.getControl().setLayoutData(new RowData(config.layout.smallButtonWidth, config.layout.smallButtonHeight));
	}

	public void setTooltip(String tooltip) {
		text.setToolTipText(tooltip);
	}

	@Override
	public Composite getButtonComposite() {
		return buttonPanel;
	}

	@Override
	public ImageRegistry getImageRegistry() {
		return config.imageRegistry;
	}

	@Override
	public IResourceGetter getResourceGetter() {
		return config.resourceGetter;
	}

	@Override
	public Control getControl() {
		return content;
	}

	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
	}

}
