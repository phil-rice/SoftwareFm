package org.softwareFm.core.plugin;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.softwareFm.arc4eclipseRepository.api.IUrlGenerator;
import org.softwareFm.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;
import org.softwareFm.core.plugin.Arc4EclipseCoreActivator;
import org.softwareFm.core.plugin.Plugins;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.IDisplayContainerForTests;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.resources.IResourceGetter;

@SuppressWarnings("restriction")
public class Arc4EclipseCoreActivatorTest extends TestCase {

	private Display display;

	public void testBasicResources() {
		checkResource("Edit", SwtBasicConstants.key);
	}

	public void testBasicImages() {
		checkImages(SwtBasicConstants.key);
		checkImages(SwtBasicConstants.helpKey);
		checkImages(SwtBasicConstants.browseKey);
		checkImages(SwtBasicConstants.deleteKey);
		checkImages(SwtBasicConstants.browseKey);
	}

	private void checkResource(String expected, String editkey) {
		Arc4EclipseCoreActivator activator = Arc4EclipseCoreActivator.getDefault();
		IResourceGetter resourceGetter = activator.getConfigForTitleAnd(display).resourceGetter;
		assertEquals(expected, Resources.getTooltip(resourceGetter, SwtBasicConstants.key));

	}

	public void testCanMakeDisplayContainers() {
		checkCanMakeDisplayContainers(RepositoryConstants.entityJar);
		checkCanMakeDisplayContainers(RepositoryConstants.entityOrganisation);
		checkCanMakeDisplayContainers(RepositoryConstants.entityProject);
	}

	private void checkCanMakeDisplayContainers(String entity) {
		Arc4EclipseCoreActivator activator = Arc4EclipseCoreActivator.getDefault();
		DisplayerContext displayerContext = new DisplayerContext(activator.getSelectedBindingManager(), activator.getRepository(), activator.getUrlGeneratorMap(), activator.getConfigForTitleAnd(display));
		activator.getDisplayContainerFactory(display, entity).create(displayerContext, new Shell(display));
	}

	public void testUrlGenerators() throws Exception {
		checkUrlGenerator("/jars/565/someurl", RepositoryConstants.entityJar);
		checkUrlGenerator("/organisations/565/someurl", RepositoryConstants.entityOrganisation);
		checkUrlGenerator("/projects/565/someurl", RepositoryConstants.entityProject);
	}

	private void checkUrlGenerator(String expected, String entity) throws Exception {
		Arc4EclipseCoreActivator activator = Arc4EclipseCoreActivator.getDefault();
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
		Arc4EclipseCoreActivator activator = Arc4EclipseCoreActivator.getDefault();
		DisplayerContext displayerContext = new DisplayerContext(activator.getSelectedBindingManager(), activator.getRepository(), activator.getUrlGeneratorMap(), activator.getConfigForTitleAnd(display));
		IDisplayContainerForTests container = (IDisplayContainerForTests) activator.getDisplayContainerFactory(display, "entity").create(displayerContext, new Shell(display));
		IRegisteredItems registeredItems = container.getRegisteredItems();
		assertNotNull(registeredItems.getLineEditor(name));

	}

	public void testAllImagesCanBeGot() {
		checkImages(SwtBasicConstants.key);
		checkImages(SwtBasicConstants.helpKey);
		final List<String> keys = Lists.newList();
		Plugins.useConfigElements(Arc4EclipseCoreActivator.IMAGE_ID, new ICallback<IConfigurationElement>() {
			@Override
			public void process(IConfigurationElement t) throws Exception {
				String key = t.getAttribute("key");
				keys.add(key);
			}
		}, ICallback.Utils.rethrow());
		checkImages(keys.toArray(new String[0]));
	}

	private void checkImages(String... keys) {
		Arc4EclipseCoreActivator activator = Arc4EclipseCoreActivator.getDefault();
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
