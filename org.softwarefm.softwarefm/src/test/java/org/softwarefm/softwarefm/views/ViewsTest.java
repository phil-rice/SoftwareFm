package org.softwarefm.softwarefm.views;

import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.eclipse.tests.SwtTest;

public class ViewsTest extends SwtTest {

	public void testViewsMakeAPanel() {
		checkView(new AllView());
		checkView(new CodeView());
		checkView(new ArtifactView());
		checkView(new DebugTextView());
	}

	private <C extends SoftwareFmComposite> void checkView(SoftwareFmView<C> view) {
		SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests();
		C panel = view.makeSoftwareFmComposite(shell, container);
		assertNotNull(panel);

	}

}
