package org.arc4eclipse.core.plugin;

import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.Displayers;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerForTests;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.utilities.maps.Maps;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;

@SuppressWarnings("restriction")
public abstract class AbstractDisplayerTest<L extends Control, C extends Control> extends TestCase {

	protected Arc4EclipseCoreActivator activator;
	protected Display display;
	private ImageRegistry imageRegistry;
	private SelectedArtifactSelectionManager selectedBindingManager;
	private DisplayerContext displayerContext;
	private Shell shell;
	private IArc4EclipseRepository repository;
	private IResourceGetter resourceGetter;

	abstract protected String getDisplayerKey();

	abstract protected String getDataKey();

	abstract protected String getSmallImageKey();

	abstract protected Object getSampleData(String key);

	abstract protected IDisplayer<L, C> getDisplayer();

	abstract protected void checkData(Object sampleData, L largeControl, C smallControl);

	public void testRegisteredDisplayer() {
		String key = getDataKey();
		String entity = "entity";
		IDisplayContainerForTests container = makeContainer(entity, key, getSmallImageKey());
		setValues(container, entity, key);
		L largeControl = container.getLargeControlFor(key);
		C smallControl = container.getSmallControlFor(key);
		checkData(getSampleData(key), largeControl, smallControl);
	}

	private void setValues(IDisplayContainerForTests container, String entity, String key) {
		Map<String, Object> data = Maps.makeLinkedMap(key, getSampleData(key));
		Map<String, Object> context = Maps.makeLinkedMap(RepositoryConstants.entity, entity);
		container.setValues(new BindingContext(RepositoryDataItemStatus.FOUND, "some url", data, context));
	}

	private IDisplayContainerForTests makeContainer(String entity, String key, String smallImageKey) {
		IDisplayContainerFactory displayContainerFactory = activator.getDisplayContainerFactory(display, entity);
		displayContainerFactory.register(Maps.<String, String> makeMap(//
				DisplayCoreConstants.key, key,//
				DisplayCoreConstants.smallImageKey, smallImageKey,//
				DisplayCoreConstants.displayer, getDisplayerKey()//
				));

		IDisplayContainerForTests container = (IDisplayContainerForTests) displayContainerFactory.create(displayerContext, shell);
		return container;
	}

	public void testSmallControlIcon() {
		Image expectedImage = imageRegistry.get(Resources.getDepressedName(getSmallImageKey()));
		IDisplayContainerForTests container = makeContainer("entity", getDataKey(), getSmallImageKey());
		Label button = container.getSmallControlFor(getDataKey());
		assertSame(expectedImage, button.getImage());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		display = Workbench.getInstance().getDisplay();
		activator = Arc4EclipseCoreActivator.getDefault();
		imageRegistry = activator.getImageRegistry(display);
		repository = activator.getRepository();
		selectedBindingManager = activator.getSelectedBindingManager();
		resourceGetter = Arc4EclipseCoreActivator.getDefault().getResourceGetter();
		displayerContext = new DisplayerContext(selectedBindingManager, repository, ConfigForTitleAnd.create(display, resourceGetter, imageRegistry));
		shell = new Shell(display);
		Images.registerImages(display, imageRegistry, Displayers.class, "Key1");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Images.removeImages(activator.getImageRegistry(display), "Key1");
		shell.dispose();
		activator.clear();
	}

}