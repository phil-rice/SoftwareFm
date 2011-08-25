package org.arc4eclipse.core.plugin;

import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.core.plugin.Arc4EclipseCoreActivator;
import org.arc4eclipse.core.plugin.SelectedArtifactSelectionManager;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.Displayers;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerForTests;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.utilities.maps.Maps;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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

	abstract protected Object getSampleData(String key);

	abstract protected IDisplayer<L, C> getDisplayer();

	abstract protected void checkData(Object sampleData, L largeControl, C smallControl);

	public void testRegisteredDisplayer() {
		String entity = "entity";
		IDisplayContainerFactory displayContainerFactory = activator.getDisplayContainerFactory(display, entity);
		String key = getDataKey();
		displayContainerFactory.register(Maps.<String, String> makeMap(//
				DisplayCoreConstants.key, key,//
				DisplayCoreConstants.displayer, getDisplayerKey()//
				));

		Object sampleData = getSampleData(key);
		Map<String, Object> data = Maps.makeLinkedMap(key, sampleData);
		Map<String, Object> context = Maps.makeLinkedMap(RepositoryConstants.entity, entity);
		IDisplayContainerForTests container = (IDisplayContainerForTests) displayContainerFactory.create(displayerContext, shell);
		container.setValues(new BindingContext("some url", data, context));
		L largeControl = container.getLargeControlFor(key);
		C smallControl = container.getSmallControlFor(key);
		checkData(sampleData, largeControl, smallControl);
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
		Images.registerImages(display, activator.getImageRegistry(), Displayers.class, "Key1");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Images.removeImages(activator.getImageRegistry(display), "Key1");
		shell.dispose();
		activator.clear();
	}

}
