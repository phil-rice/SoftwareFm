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
import org.softwareFm.softwareFmImages.title.TitleAnchor;

public class BasicImageRegisterConfigurator implements IImageRegisterConfigurator {

	@Override
	public void registerWith(Device device, ImageRegistry imageRegistry) {
		Images.registerImages(device, imageRegistry, BackdropAnchor.class, "backdrop", //
				"main", "depressed", //
				"group1", "group1Active",//
				"group2", "group2Active",//
				"group3", "group3Active",//
				"group4", "group4Active",//
				"group5", "group5Active",//
				"group6", "group6Active");
		Images.registerImages(device, imageRegistry, ArtifactsAnchor.class, "artifact", //
				"jar", "jarClearEclipse", "jarCopyFromSoftwareFm", "jarCopyToSoftwareFm", //
				"blog", "document", "faceBook", "forum", "javadoc", "issues", "license", "mailingList", "merchandise", "organisation", "project",//
				"news", "rss", "recruitment", "source", "tutorials", "twitter");
		Images.registerImages(device, imageRegistry, OverlaysAnchor.class, "overlay", "add", "delete", "edit", "properties");
		Images.registerImages(device, imageRegistry, SmallIconsAnchor.class, "smallIcon", "softwareFm", "javadoc", "source");
		Images.registerImages(device, imageRegistry, GeneralAnchor.class, "general", "browse", "help", "clear", "search", "maven", "sfmLogo");
		Images.registerImages(device, imageRegistry, TitleAnchor.class, "title", "artifact", "folder", "group", "history", "jar2", "next", "previous", "refresh", "version");

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
