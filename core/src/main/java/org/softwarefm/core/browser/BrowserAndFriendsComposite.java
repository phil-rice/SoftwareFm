package org.softwarefm.core.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.functions.IFunction1;

public class BrowserAndFriendsComposite extends BrowserComposite {

	public BrowserAndFriendsComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	protected void addMoreControls() {
		new Label(rowComposite, SWT.NULL).setText("Goes here");
	}
	public static void main(String[] args) {
		Swts.Show.display(BrowserAndFriendsComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(from.getDisplay());
				BrowserAndFriendsComposite browserComposite = new BrowserAndFriendsComposite(from, container);
				browserComposite.setUrl("www.google.com");
				return browserComposite.getComposite();
			}
		});
	}
}
