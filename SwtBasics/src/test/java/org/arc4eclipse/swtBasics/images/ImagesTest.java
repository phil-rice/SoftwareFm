package org.arc4eclipse.swtBasics.images;

import junit.framework.TestCase;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.SwtTestFixture;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;

public class ImagesTest extends TestCase {

	public void testImages() {
		Display display = SwtTestFixture.shell().getDisplay();
		// need display around before we can make an imageRegistry

		// its useful just creating them and checking they are not null
		ImageRegistry registry = new ImageRegistry();
		checkKey(display, registry, SwtBasicConstants.editKey);
		checkKey(display, registry, SwtBasicConstants.helpKey);
	}

	public void testWithBasics() {
		Display display = SwtTestFixture.shell().getDisplay();
		ImageRegistry withBasics = Images.withBasics(display);
		assertNotNull(Images.getMainImage(withBasics, SwtBasicConstants.editKey));
		assertNotNull(Images.getDepressedImage(withBasics, SwtBasicConstants.editKey));
		assertNotNull(Images.getMainImage(withBasics, SwtBasicConstants.helpKey));
		assertNotNull(Images.getDepressedImage(withBasics, SwtBasicConstants.helpKey));

	}

	private void checkKey(Display display, ImageRegistry registry, String editkey) {
		Images.registerImages(display, registry, getClass(), editkey);
		assertNotNull(Images.getMainImage(registry, editkey));
		assertNotNull(Images.getDepressedImage(registry, editkey));
	}

}
