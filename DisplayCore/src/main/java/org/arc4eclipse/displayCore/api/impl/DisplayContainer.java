package org.arc4eclipse.displayCore.api.impl;

import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.NameSpaceNameValueAndDisplayer;
import org.arc4eclipse.swtBasics.Swts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayContainer implements IDisplayContainer {

	private final Composite parent;
	private final Composite content;
	private Composite current;
	private final int style;

	public DisplayContainer(Composite parent, int style) {
		this.parent = parent;
		this.style = style;
		this.content = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		content.setLayout(layout);

	}

	@Override
	public void dispose() {
		content.dispose();
		if (current != null)
			current.dispose();
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public void addDisplayers(final List<NameSpaceNameValueAndDisplayer> toBeDisplayed, final BindingContext bindingContext) {
		parent.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Map<String, Object> data = bindingContext.data;
				String url = bindingContext.url;
				makeCurrent();
				for (NameSpaceNameValueAndDisplayer nameSpaceNameValueAndDisplayer : toBeDisplayed)
					nameSpaceNameValueAndDisplayer.displayer.makeCompositeAsChildOf(current, bindingContext, nameSpaceNameValueAndDisplayer);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(current);
				current.redraw();
				// System.out.println("Parent " + Swts.layoutAsString(parent));
				// System.out.println("Current " + Swts.layoutAsString(current));
				// for (Control child : current.getChildren())
				// System.out.println("Child " + Swts.layoutAsString(child));
				parent.layout();
				parent.redraw();
			}
		});
	}

	private void makeCurrent() {
		if (current != null) {
			for (Control child : current.getChildren())
				child.dispose();
			current.dispose();
		}
		current = new Composite(content, style);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		current.setLayout(layout);
		current.setLayoutData(Swts.makeGrabHorizonalAndFillGridData());
	}
}
