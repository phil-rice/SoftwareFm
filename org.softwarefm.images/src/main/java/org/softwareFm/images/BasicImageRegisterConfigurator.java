/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.images;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.common.functions.IFunction1WithDispose;
import org.softwareFm.images.artifacts.ArtifactsAnchor;
import org.softwareFm.images.general.GeneralAnchor;
import org.softwareFm.images.login.LoginAnchor;
import org.softwareFm.images.title.TitleAnchor;

public class BasicImageRegisterConfigurator implements IImageRegisterConfigurator {

	@Override
	public void registerWith(Device device, ImageRegistry imageRegistry) {
		Images.registerImages(device, imageRegistry, ArtifactsAnchor.class, "artifact", "archive", "advert",//
				"article", "jar",  "jarUnrecognised", //
				"comment", "company", "description", "document", "email", "faceBook", "folder", "forum", "javadoc", //
				"issues", "job", "license", "mailingList", "merchandise", "nothing", "name", "group", "artifact",//
				"news", "rss", "source", "snippet", "subscribe", "tutorial", "twitter", "unsubscribe");
		Images.registerImages(device, imageRegistry, GeneralAnchor.class, "general", //
				"browse", "help", "clear", "sfmLogo", "commentAdd", "cancel", "ok");
		Images.registerImages(device, imageRegistry, LoginAnchor.class, "login", //
				"login", "changePassword", "forgotPassword", "loginButton", "signUp");
		Images.registerImages(device, imageRegistry, TitleAnchor.class, "title",//
				"advert", "artifact", "blog", "document", "facebook", "folder", "forum", "group", "history", "jar", //
				"mailingList", "next", "previous", "refresh", "rss", "tutorial", "twitter", "version");

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
	public static IFunction1WithDispose<String, Image> imageFnForTests(Composite from) {
		final ImageRegistry imageRegistry = forTests(from);
		return new IFunction1WithDispose<String, Image>() {
			@Override
			public Image apply(String from) throws Exception {
				return imageRegistry.get(from);
			}

			@Override
			public void dispose() {
				imageRegistry.dispose();
			}
		};
	}
}