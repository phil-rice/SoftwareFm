package org.softwareFm.softwareFmImages.images;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Device;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.backdrop.BackdropAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.swtBasics.images.Images;

public class SoftwareFmImages {
	public static ImageRegistry withBasics(Device device) {
		ImageRegistry imageRegistry = new ImageRegistry();
		Images.registerImagesInDirectory(device, imageRegistry, ArtifactsAnchor.class, "artifact", "jar");
		Images.registerImagesInDirectory(device, imageRegistry, BackdropAnchor.class, "backdrop", "main");
		Images.registerImagesInDirectory(device, imageRegistry, OverlaysAnchor.class, "overlay", "add");
		Images.registerImagesInDirectory(device, imageRegistry, SmallIconsAnchor.class, "smallIcon", "javadoc");

		return imageRegistry;
	}
}
