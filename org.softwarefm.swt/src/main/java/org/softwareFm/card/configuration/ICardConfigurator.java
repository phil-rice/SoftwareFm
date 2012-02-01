/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.configuration;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.internal.BasicCardConfigurator;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.softwareFmImages.IImageRegisterConfigurator;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.functions.IFunction1WithDispose;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.resources.NestedResourceGetterFn;

public interface ICardConfigurator {

	public CardConfig configure(Display device, CardConfig config);

	public static class Utils {

		public static ICardConfigurator basicConfigurator() {
			return new BasicCardConfigurator();
		}

		public static IFunction1<String, IResourceGetter> resourceGetterFn(Class<?> anchorClass, String rootName) {
			final IResourceGetter baseResourceGetter = IResourceGetter.Utils.noResources().with(anchorClass, rootName);
			final IFunction1<String, IResourceGetter> resourceGetterFn = new NestedResourceGetterFn(baseResourceGetter, CardConfig.class);
			return resourceGetterFn;
		}

		public static IFunction1WithDispose<String, Image> imageFn(Display display, IImageRegisterConfigurator configurator) {
			final ImageRegistry imageRegistry = new ImageRegistry(display);
			configurator.registerWith(display, imageRegistry);
			IFunction1WithDispose<String, Image> imageFn = new IFunction1WithDispose<String, Image>() {
				@Override
				public Image apply(String from) throws Exception {
					return imageRegistry.get(from);
				}

				@Override
				public void dispose() {
					imageRegistry.dispose();
				}
			};
			return imageFn;
		}

		public static CardConfig cardConfigForTests(Display display) {
			CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(display, new CardConfig(ICardFactory.Utils.noCardFactory(), ICardDataStore.Utils.mock()));
			return cardConfig;
		}
	}

}