package org.softwareFm.core.plugin;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.displayCore.api.IDisplayContainerForTests;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.api.IUrlGeneratorMap;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.resources.IResourceGetter;

@SuppressWarnings("restriction")
public class SoftwareFmActivatorTest extends TestCase {

	private Display display;

	public void testBasicResources() {
		checkResource("Edit", SwtBasicConstants.editKey);
	}

	public void testBasicImages() {
		checkImages(SwtBasicConstants.editKey);
		checkImages(SwtBasicConstants.helpKey);
		checkImages(SwtBasicConstants.browseKey);
		checkImages(SwtBasicConstants.deleteKey);
		checkImages(SwtBasicConstants.browseKey);
	}

	private void checkResource(String expected, String editkey) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		IResourceGetter resourceGetter = activator.getConfigForTitleAnd(display).resourceGetter;
		assertEquals(expected, Resources.getTooltip(resourceGetter, SwtBasicConstants.editKey));

	}

	public void testCanMakeDisplayContainers() {
		checkCanMakeDisplayContainers(RepositoryConstants.viewJar, RepositoryConstants.entityJar);
		checkCanMakeDisplayContainers(RepositoryConstants.viewSummaryJar, RepositoryConstants.entityJar);
		checkCanMakeDisplayContainers(RepositoryConstants.viewOrganisation, RepositoryConstants.entityOrganisation);
		checkCanMakeDisplayContainers(RepositoryConstants.viewProject, RepositoryConstants.entityProject);
	}

	private void checkCanMakeDisplayContainers(String view, String entity) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		DisplayerContext displayerContext = new DisplayerContext(activator.getSelectedBindingManager(), activator.getRepository(), activator.getUrlGeneratorMap(), activator.getConfigForTitleAnd(display));
		IDisplayContainerFactory displayContainerFactory = activator.getDisplayContainerFactory(display, view, entity);
		Shell parent = new Shell(display);
		displayContainerFactory.create(displayerContext, parent);
	}

	public void testUrlGenerators() throws Exception {
		checkUrlGenerator("/jars/565/someurl", RepositoryConstants.entityJar);
		checkUrlGenerator("/organisations/565/someurl", RepositoryConstants.entityOrganisation);
		checkUrlGenerator("/projects/565/someurl", RepositoryConstants.entityProject);
	}

	private void checkUrlGenerator(String expected, String entity) throws Exception {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		IUrlGeneratorMap urlGeneratorMap = activator.getUrlGeneratorMap();
		IUrlGenerator urlGenerator = urlGeneratorMap.get(entity);
		assertEquals(expected, urlGenerator.apply("someurl"));
	}

	public void testLineEditors() {
		checkLineEditor("nameUrl");
		checkLineEditor("nameValue");
		checkLineEditor("tweet");
	}

	private void checkLineEditor(String name) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		DisplayerContext displayerContext = new DisplayerContext(activator.getSelectedBindingManager(), activator.getRepository(), activator.getUrlGeneratorMap(), activator.getConfigForTitleAnd(display));
		IDisplayContainerForTests container = (IDisplayContainerForTests) activator.getDisplayContainerFactory(display, "view", "entity").create(displayerContext, new Shell(display));
		IRegisteredItems registeredItems = container.getRegisteredItems();
		assertNotNull(registeredItems.getLineEditor(name));

	}

	public void testAllImagesCanBeGot() {
		checkImages(SwtBasicConstants.editKey);
		checkImages(SwtBasicConstants.helpKey);
		final List<String> keys = Lists.newList();
		Plugins.useConfigElements(SoftwareFmActivator.IMAGE_ID, new ICallback<IConfigurationElement>() {
			@Override
			public void process(IConfigurationElement t) throws Exception {
				String key = t.getAttribute("editKey");
				keys.add(key);
			}
		}, ICallback.Utils.rethrow());
		checkImages(keys.toArray(new String[0]));
	}

	private void checkImages(String... keys) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		ImageRegistry imageRegistry = activator.getConfigForTitleAnd(display).imageRegistry;
		for (String key : keys) {
			System.out.println("Checking image for: " + key);
			assertNotNull(Images.getMainImage(imageRegistry, key));
			assertNotNull(Images.getDepressedImage(imageRegistry, key));
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		display = Workbench.getInstance().getDisplay();
	}

}
