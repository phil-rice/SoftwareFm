package org.arc4eclipse.displayCore.api.impl;

import java.util.List;

import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.ITitleLookup;
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
	public Composite getComposite() {
		return content;
	}

	@Override
	public void addDisplayers(final ITitleLookup titleLookup, final List<NameSpaceNameValueAndDisplayer> toBeDisplayed) {
		parent.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				makeCurrent();
				for (NameSpaceNameValueAndDisplayer nameSpaceNameValueAndDisplayer : toBeDisplayed)
					nameSpaceNameValueAndDisplayer.displayer.makeCompositeAsChildOf(titleLookup, current, nameSpaceNameValueAndDisplayer.nameSpaceNameAndValue);
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
