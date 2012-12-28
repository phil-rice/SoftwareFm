package org.softwarefm.softwarefm.views;

import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.SoftwareFmComposite;
import org.softwarefm.core.tests.SwtTest;

public class ViewsTest extends SwtTest {

	public void testViewsMakeAPanel() {
		checkView(new AllView());
		checkView(new CodeView());
		checkView(new ArtifactView());
		checkView(new DebugTextView());
	}

	private <C extends SoftwareFmComposite> void checkView(SoftwareFmView<C> view) {
		SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(display);
		C panel = view.makeSoftwareFmComposite(shell, container);
		assertNotNull(panel);

	}

}
