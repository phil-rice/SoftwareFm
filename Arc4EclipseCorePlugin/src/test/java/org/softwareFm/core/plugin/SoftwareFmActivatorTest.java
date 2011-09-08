package org.softwareFm.core.plugin;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.Workbench;
import org.softwareFm.utilities.tests.Tests;

@SuppressWarnings("restriction")
public class SoftwareFmActivatorTest extends TestCase {

	private static Display display;

	public void testBasicResources() {
	}

	//
	// public void testBasicImages() {
	// checkImages(SwtBasicConstants.editKey);
	// checkImages(SwtBasicConstants.helpKey);
	// checkImages(SwtBasicConstants.browseKey);
	// checkImages(SwtBasicConstants.deleteKey);
	// checkImages(SwtBasicConstants.browseKey);
	// }
	//
	// private void checkResource(String expected, String editkey) {
	// SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
	// IResourceGetter resourceGetter = activator.getConfigForTitleAnd(display).resourceGetter;
	// assertEquals(expected, Resources.getTooltip(resourceGetter, SwtBasicConstants.editKey));
	//
	// }
	//
	// public void testCanMakeDisplayContainers() {
	// checkCanMakeDisplayContainers(RepositoryConstants.viewJar, RepositoryConstants.entityJar);
	// checkCanMakeDisplayContainers(RepositoryConstants.viewSummaryJar, RepositoryConstants.entityJar);
	// checkCanMakeDisplayContainers(RepositoryConstants.viewOrganisation, RepositoryConstants.entityOrganisation);
	// checkCanMakeDisplayContainers(RepositoryConstants.viewProject, RepositoryConstants.entityProject);
	// }
	//
	// private void checkCanMakeDisplayContainers(String view, String entity) {
	// SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
	// DisplayerContext displayerContext = new DisplayerContext(activator.getSelectedBindingManager(), activator.getRepository(), activator.getUrlGeneratorMap(), activator.getConfigForTitleAnd(display));
	// IDisplayContainerFactory displayContainerFactory = activator.getDisplayContainerFactory(display, view, entity);
	// Shell parent = new Shell(display);
	// displayContainerFactory.create(displayerContext, parent);
	// }
	//
	// public void testUrlGenerators() throws Exception {
	// checkUrlGenerator("/jars/565/someurl", RepositoryConstants.entityJar);
	// checkUrlGenerator("/organisations/565/someurl", RepositoryConstants.entityOrganisation);
	// checkUrlGenerator("/projects/565/someurl", RepositoryConstants.entityProject);
	// }
	//
	// private void checkUrlGenerator(String expected, String entity) throws Exception {
	// SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
	// IUrlGeneratorMap urlGeneratorMap = activator.getUrlGeneratorMap();
	// IUrlGenerator urlGenerator = urlGeneratorMap.get(entity);
	// assertEquals(expected, urlGenerator.apply("someurl"));
	// }
	//
	// public void testLineEditors() {
	// checkLineEditor("nameUrl");
	// checkLineEditor("nameValue");
	// checkLineEditor("tweetKey");
	// }
	//
	// private void checkLineEditor(String name) {
	// SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
	// DisplayerContext displayerContext = new DisplayerContext(activator.getSelectedBindingManager(), activator.getRepository(), activator.getUrlGeneratorMap(), activator.getConfigForTitleAnd(display));
	// IDisplayContainerForTests container = (IDisplayContainerForTests) activator.getDisplayContainerFactory(display, "view", "entity").create(displayerContext, new Shell(display));
	// IRegisteredItems registeredItems = container.getRegisteredItems();
	// assertNotNull(registeredItems.getLineEditor(name));
	//
	// }

	public void testAllImagesCanBeGot() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		checkImages("backdrop", "main", "depressed");
		checkImages("artifact", "jar", "jarClearEclipse", "jarCopyFromSoftwareFm", "jarCopyToSoftwareFm", "organisation", "project");
		checkImages("smallIcon", "softwareFm", "javadoc", "source");
		checkImages("overlay", "add", "delete", "edit");
		checkImages("general", "browse", "help");
	}

	private void checkImages(String prefix, String... names) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		ImageRegistry imageRegistry = activator.getConfigForTitleAnd(display).imageRegistry;
		for (String name : names) {
			String fullName = prefix + "." + name;
			assertNotNull(fullName, imageRegistry.get(fullName));
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (display == null) {
			Workbench instance = Workbench.getInstance();
			if (instance == null) {
				display = new Display();
				SoftwareFmActivator.createForTest();
			} else
				display = instance.getDisplay();
		}
	}

	public static void main(String[] args) {
		Tests.executeTest(SoftwareFmActivatorTest.class);
	}

}
