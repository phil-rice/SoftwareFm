package org.softwarefm.core.browser;

import org.easymock.EasyMock;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.tests.SwtTest;

public class BrowserCompositeTest extends SwtTest {

	public final static String nonEditingUrl1 = "www.google.co.uk";
	public final static String nonEditingUrl2 = "www.softwarefm.org";
	public final static String editingUrl1 = "http://www.softwarefm.org/mediawiki/index.php?title=Code:Java.lang/Object/finalize&action=edit";

	private IEditingListener listener1;
	private IEditingListener listener2;
	private BrowserComposite browserComposite;

	public void testEditingListenersGetCalledWhenStartEditing() {
		addListeners();
		listener1.editingState(true);
		listener2.editingState(true);
		replay();
		browserComposite.setUrlAndWait(editingUrl1);
		verify();
	}

	public void testEditingListenersGetCalledWhenStopEditing() {
		addListeners();
		listener1.editingState(true);
		listener2.editingState(true);
		listener1.editingState(false);
		listener2.editingState(false);
		replay();
		browserComposite.setUrlAndWait(editingUrl1);
		browserComposite.setUrlAndWait(nonEditingUrl1);
		browserComposite.setUrlAndWait(nonEditingUrl2);
		verify();
	}

	public void testEditingListenerDoNotGetCalledInitially() {
		addListeners();
		replay();
		browserComposite.setUrlAndWait(nonEditingUrl1);
		browserComposite.setUrlAndWait(nonEditingUrl2);
		verify();
	}

	private void verify() {
		EasyMock.verify(listener1, listener2);
	}

	private void replay() {
		EasyMock.replay(listener1, listener2);
	}

	private void addListeners() {
		browserComposite.addEditingListener(listener1);
		browserComposite.addEditingListener(listener2);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		listener1 = EasyMock.createMock(IEditingListener.class);
		listener2 = EasyMock.createMock(IEditingListener.class);
		SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(display);
		browserComposite = new BrowserComposite(shell, container);
	}

}
