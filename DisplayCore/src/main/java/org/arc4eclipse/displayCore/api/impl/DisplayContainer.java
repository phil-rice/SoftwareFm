package org.arc4eclipse.displayCore.api.impl;

import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.NameSpaceNameValueAndDisplayer;
import org.eclipse.swt.layout.FormLayout;
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
		this.content = new Composite(parent, style);
		content.setLayout(new FormLayout());
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
	public void addDisplayers(final BindingContext bindingContext, final Map<String, Object> data, final List<NameSpaceNameValueAndDisplayer> toBeDisplayed) {
		parent.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				makeCurrent();
				for (NameSpaceNameValueAndDisplayer nameSpaceNameValueAndDisplayer : toBeDisplayed)
					nameSpaceNameValueAndDisplayer.displayer.makeCompositeAsChildOf(current, bindingContext, data, nameSpaceNameValueAndDisplayer);
				current.pack();
				current.redraw();
				System.out.println("Parent " + parent + " " + parent.getSize());
				System.out.println("Current " + current + " " + current.getSize());
				for (Control child : current.getChildren())
					System.out.println("Child " + child + " " + child.getSize());
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
		current.setLayout(new GridLayout());
	}
}
