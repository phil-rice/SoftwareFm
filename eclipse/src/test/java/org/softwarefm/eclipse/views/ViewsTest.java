package org.softwarefm.eclipse.views;

import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.composite.SoftwareFmComposite;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.eclipse.views.AllView;
import org.softwarefm.eclipse.views.ArtifactView;
import org.softwarefm.eclipse.views.CodeView;
import org.softwarefm.eclipse.views.DebugTextView;
import org.softwarefm.eclipse.views.SoftwareFmView;

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
