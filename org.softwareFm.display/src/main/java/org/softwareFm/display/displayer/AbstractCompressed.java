package org.softwareFm.display.displayer;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public abstract class AbstractCompressed<T extends Control> implements IDisplayer {
	protected final CompositeConfig config;
	protected final Composite content;
	protected final Composite buttonPanel;
	protected final T control;
	private boolean shouldBeEnabled;

	public AbstractCompressed(Composite parent, int style, CompositeConfig config) {
		this.config = config;
		this.content = Swts.newComposite(parent, style, getClass().getSimpleName());
		this.buttonPanel = Swts.newComposite(content, style, "ButtonPanel");
		this.control = makeControl(content);
		setLayout();
	}

	abstract protected void setLayout();

	abstract protected T makeControl(Composite parent);

	@Override
	public void addClickListener(final Listener listener) {
		control.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				listener.handleEvent(new Event());
			}
		});
	}

	@Override
	public void buttonAdded(IHasControl button) {
		button.getControl().setLayoutData(new RowData(config.layout.smallButtonWidth, config.layout.smallButtonHeight));
	}

	public void setTooltip(String tooltip) {
		control.setToolTipText(tooltip);
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
		this.shouldBeEnabled = enabled;
		control.setEnabled(enabled);
	}

	public boolean isShouldBeEnabled() {
		return shouldBeEnabled;
	}

}
