package org.arc4eclipse.core.plugin;

import java.util.List;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainerForTests;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;

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
