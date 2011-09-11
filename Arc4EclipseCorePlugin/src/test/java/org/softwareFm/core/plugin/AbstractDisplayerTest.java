package org.softwareFm.core.plugin;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.displayCore.api.IDisplayContainerForTests;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGeneratorMap;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

@SuppressWarnings("restriction")
public abstract class AbstractDisplayerTest<L extends IHasControl, C extends IHasControl> extends TestCase {

	protected SoftwareFmActivator activator;
	protected Display display;
	private ImageRegistry imageRegistry;
	private SelectedArtifactSelectionManager selectedBindingManager;
	private DisplayerContext displayerContext;
	private Shell shell;
	private ISoftwareFmRepository repository;
	private IResourceGetter resourceGetter;
	private IUrlGeneratorMap urlGeneratorMap;

	abstract protected String getDisplayerKey();

	abstract protected String getDataKey();

	abstract protected String getSmallImageKey();

	abstract protected Object getSampleData(String key);

	abstract protected IDisplayer<L, C> getDisplayer();

	abstract protected void checkData(Object sampleData, L largeControl, C smallControl);

	public void testRegisteredDisplayer() {
		String key = getDataKey();
		String entity = "entity";
		String viewName = "viewKey";
		IDisplayContainerForTests container = makeContainer(viewName, entity, key, getSmallImageKey());
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

	private IDisplayContainerForTests makeContainer(String viewName, String entity, String key, String smallImageKey) {
		IDisplayContainerFactory displayContainerFactory = activator.getDisplayContainerFactory(display, viewName);
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
		IDisplayContainerForTests container = makeContainer("view", "entity", getDataKey(), getSmallImageKey());
		Label button = container.getSmallControlFor(getDataKey());
		assertSame(expectedImage, button.getImage());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		display = Workbench.getInstance().getDisplay();
		activator = SoftwareFmActivator.getDefault();
		imageRegistry = activator.getImageRegistry(display);
		repository = activator.getRepository();
		selectedBindingManager = activator.getSelectedBindingManager();
		resourceGetter = SoftwareFmActivator.getDefault().getResourceGetter();
		urlGeneratorMap = activator.getUrlGeneratorMap();
		displayerContext = new DisplayerContext(selectedBindingManager, repository, urlGeneratorMap, ConfigForTitleAnd.create(display, resourceGetter, imageRegistry));
		shell = new Shell(display);
		Images.registerImage(display, imageRegistry, Displayers.class, "Key1");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Images.removeImage(activator.getImageRegistry(display), "Key1");
		shell.dispose();
		activator.clear();
	}

}
