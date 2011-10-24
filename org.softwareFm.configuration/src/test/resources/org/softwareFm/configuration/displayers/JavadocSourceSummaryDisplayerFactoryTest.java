package org.softwareFm.configuration.displayers;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.display.displayer.CompressedText;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.resources.IResourceGetter;

public class JavadocSourceSummaryDisplayerFactoryTest extends TestCase {

	private Shell shell;
	private IResourceGetter resourceGetter;
	private CompositeConfig compositeConfig;

	public void testTitle() {
		checkTitle("javadocEclipse", "javadocRemote", "doesntMatchValue");
		checkTitle("javadocEclipse", null, "eclipseNotUrl");
		checkTitle("javadocEclipse", "", "eclipseNotUrl");
		checkTitle("", "", "noData");
		checkTitle(null, null, "noData");
	}

	private void checkTitle(String eclipse, String softwareFm, String expected) {
		JavadocSourceSummaryDisplayerFactory displayerFactory = new JavadocSourceSummaryDisplayerFactory("data.raw.jar.javadoc", "data.jar.javadoc");
		DisplayerDefn defn = new DisplayerDefn(displayerFactory).noIcon();
		DataGetterMock dataGetter = new DataGetterMock("data.raw.jar.javadoc", eclipse, "data.jar.javadoc", softwareFm);
		ActionContext actionContext = new ActionContext(null, null, dataGetter, null, compositeConfig, null, null, null, null);

		CompressedText displayer = (CompressedText) defn.createDisplayer(shell, actionContext);
		displayerFactory.data(actionContext, defn, displayer, "entity", "some url");

		assertEquals(expected, displayer.getText());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = new Shell();
		resourceGetter = IResourceGetter.Utils.noResources().with(new ResourceGetterMock(//
				"button.doesntMatches.title", "doesntMatchValue",//
				"button.eclipseNotUrl.title", "eclipseNotUrl",//
				"button.noData.title", "noData"));
		compositeConfig = new CompositeConfig(shell.getDisplay(), new SoftwareFmLayout(), BasicImageRegisterConfigurator.forTests(shell), resourceGetter);
	}

	@Override
	protected void tearDown() throws Exception {
		shell.dispose();
		super.tearDown();
	}
}
