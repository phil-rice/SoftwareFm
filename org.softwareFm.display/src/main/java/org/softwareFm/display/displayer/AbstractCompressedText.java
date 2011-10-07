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

	public AbstractCompressedText(Composite parent, int style, CompositeConfig config) {
		this.config = config;
		this.content = Swts.newComposite(parent, style, CompressedText.class.getSimpleName());
		this.buttonPanel = Swts.newComposite(content, style, "ButtonPanel");
		this.text = makeTextControl(content, SWT.NULL);

		setLayout();
		
	}

	abstract protected void setLayout();

	abstract protected T makeTextControl(Composite parent, int style);

	abstract public void setText(String text);

	abstract public String getText();

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

}
