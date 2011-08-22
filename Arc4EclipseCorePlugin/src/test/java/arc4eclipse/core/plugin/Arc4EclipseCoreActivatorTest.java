package arc4eclipse.core.plugin;

import java.util.List;

import junit.framework.TestCase;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.tests.Tests;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.Workbench;

public class Arc4EclipseCoreActivatorTest extends TestCase {

	private Display display;

	public void testGetDisplayManagerCreatedImages() {
		Arc4EclipseCoreActivator activator = Arc4EclipseCoreActivator.getDefault();
		final ImageRegistry imageRegistry = activator.getImageRegistry();
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				Images.getMainImage(imageRegistry, SwtBasicConstants.editKey);
			}
		});
		assertEquals("Cannot find image with name Edit.main", e.getMessage());
		checkImages(SwtBasicConstants.editKey);
	}

	public void testAllImagesCanBeGot() {
		checkImages(SwtBasicConstants.editKey);
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
		activator.getDisplayContainerFactory(display);
		final ImageRegistry imageRegistry = activator.getImageRegistry();
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
