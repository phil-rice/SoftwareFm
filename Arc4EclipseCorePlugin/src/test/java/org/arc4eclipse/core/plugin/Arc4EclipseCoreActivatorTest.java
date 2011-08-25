package org.arc4eclipse.core.plugin;

import java.util.List;

import junit.framework.TestCase;

import org.arc4eclipse.core.plugin.Arc4EclipseCoreActivator;
import org.arc4eclipse.core.plugin.Plugins;
import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
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
