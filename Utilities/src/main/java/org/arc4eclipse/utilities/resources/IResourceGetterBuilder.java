package org.arc4eclipse.utilities.resources;

import java.util.Locale;
import java.util.ResourceBundle;

public interface IResourceGetterBuilder {

	IResourceGetterBuilder register(ResourceBundle resourceBundle);

	IResourceGetter build();

	public static class Utils {
		public static IResourceGetterBuilder builder() {
			return new ResourceGetterBuilder();
		}

		public static IResourceGetterBuilder getter(String... names) {
			return getter(Locale.getDefault(), names);

		}

		public static IResourceGetterBuilder getter(Locale locale, String... names) {
			IResourceGetterBuilder builder = builder();
			for (String name : names)
				builder.register(ResourceBundle.getBundle(name, locale));
			return builder;
		}
	}

}
