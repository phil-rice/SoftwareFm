package org.softwareFm.card.configuration;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.configuration.internal.BasicCardConfigurator;
import org.softwareFm.softwareFmImages.IImageRegisterConfigurator;
import org.softwareFm.utilities.functions.IFunction1;
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

		public static IFunction1<String, Image> imageFn(Display display, IImageRegisterConfigurator configurator) {
			final ImageRegistry imageRegistry = new ImageRegistry(display);
			configurator.registerWith(display, imageRegistry);
			IFunction1<String, Image> imageFn = new IFunction1<String, Image>() {
				@Override
				public Image apply(String from) throws Exception {
					return imageRegistry.get(from);
				}
			};
			return imageFn;
		}
	}

}
