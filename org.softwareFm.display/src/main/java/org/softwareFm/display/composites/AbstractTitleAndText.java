package org.softwareFm.display.composites;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractTitleAndText<T extends Control> extends TitleAnd {
	protected final T text;

	public AbstractTitleAndText(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey, int height) {
		super(config, parent, titleOrTitleKey, titleIsKey);
		text = makeText();
		text.setLayoutData(new RowData(config.layout.valueWidth, height));
	}

	public void setTooltip(String tooltip) {
		text.setToolTipText(tooltip);
	}

	abstract protected T makeText();

	abstract public void addModifyListener(final ModifyListener listener);

	abstract public void removeModifyListener(final ModifyListener listener);

	abstract public void addCrListener(final Listener listener);

	abstract public String getText();

	abstract public void setText(String text);

}
