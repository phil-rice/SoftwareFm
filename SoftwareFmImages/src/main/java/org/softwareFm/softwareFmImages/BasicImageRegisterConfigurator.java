package org.softwareFm.softwareFmImages;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.backdrop.BackdropAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.swtBasics.images.Images;

public class BasicImageRegisterConfigurator implements IImageRegisterConfigurator {

	@Override
	public void registerWith(Device device, ImageRegistry imageRegistry) {
		Images.registerImages(device, imageRegistry, BackdropAnchor.class, "backdrop", "main", "depressed");
		Images.registerImages(device, imageRegistry, ArtifactsAnchor.class, "artifact", //
				"jar", "jarClearEclipse", "jarCopyFromSoftwareFm", "jarCopyToSoftwareFm", //
				"document", "issues", "license", "mailingList", "merchandise", "organisation", "project",//
				"news", "recruitment", "tutorials", "twitter");
		Images.registerImages(device, imageRegistry, OverlaysAnchor.class, "overlay", "add", "delete", "edit");
		Images.registerImages(device, imageRegistry, SmallIconsAnchor.class, "smallIcon", "softwareFm", "javadoc", "source");
		Images.registerImages(device, imageRegistry, GeneralAnchor.class, "general", "browse", "help", "clear");

	}

	public static void main(String[] args) {
		Display device = new Display();
		ImageRegistry imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(device, imageRegistry);
		System.out.println(imageRegistry.get("artifact.jar"));
		System.out.println(imageRegistry.get("backdrop.main"));
		System.out.println(imageRegistry.get("general.browse"));
	}

	public static ImageRegistry forTests(Composite from) {
		ImageRegistry imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
		return imageRegistry;
	}
}
