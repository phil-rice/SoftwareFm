/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
		Images.registerImages(device, imageRegistry, ArtifactsAnchor.class, "artifact", "archive", "advert",//
				"jar", "jarClearEclipse", "jarCopyFromSoftwareFm", "jarCopyToSoftwareFm", //
				"blog","comment", "description", "document","email", "faceBook", "folder", "forum", "javadoc", //
				"issues", "license", "mailingList", "merchandise", "nothing", "name", "group", "artifact",//
				"news", "rss", "recruitment", "source", "subscribe", "tutorials", "twitter", "unsubscribe");
		Images.registerImages(device, imageRegistry, OverlaysAnchor.class, "overlay", //
				"add", "delete", "edit", "properties");
		Images.registerImages(device, imageRegistry, SmallIconsAnchor.class, "smallIcon", //
				"softwareFm", "javadoc", "source");
		Images.registerImages(device, imageRegistry, GeneralAnchor.class, "general", //
				"browse", "help", "clear", "sfmLogo");
		Images.registerImages(device, imageRegistry, TitleAnchor.class, "title",//
				"artifact", "blog", "facebook", "folder", "group", "history", "jar", "mailingList", "next", "previous", "refresh", "rss", "tutorial", "twitter", "version");

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