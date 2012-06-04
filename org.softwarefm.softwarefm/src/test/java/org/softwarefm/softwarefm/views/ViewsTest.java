package org.softwarefm.softwarefm.views;

import org.softwarefm.eclipse.BundleMarker;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.SwtTest;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.resources.IResourceGetter;

public class ViewsTest extends SwtTest {

	public void testViewsMakeAPanel() {
		checkView(new AllView());
		checkView(new ClassAndMethodView());
		checkView(new DigestView());
		checkView(new ProjectView());
		checkView(new SoftwareFmDebugView());
		checkView(new SwtDigestView());
		checkView(new VersionView());
	}

	private <C extends SoftwareFmComposite> void checkView(SoftwareFmView<C> view) {
		SoftwareFmContainer<Object> container = new SoftwareFmContainer<Object>(IResourceGetter.Utils.resourceGetter(BundleMarker.class, "text"), ISelectedBindingManager.Utils.noManager(), ICallback.Utils.<String> exception("No POM Button"));
		C panel = view.makePanel(shell, container);
		assertNotNull(panel);

	}

}
