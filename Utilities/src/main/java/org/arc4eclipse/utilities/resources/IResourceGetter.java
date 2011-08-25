package org.arc4eclipse.utilities.resources;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.arc4eclipse.utilities.constants.UtilityMessages;

public interface IResourceGetter {

	String getStringOrNull(String fullKey);

	IResourceGetter with(IResourceGetter getter);

	IResourceGetter with(Class<?> anchorClass, String propertyName);

	IResourceGetter with(ResourceBundle bundle);

	public static class Utils {
		public static IResourceGetter noResources() {
			return new ResourceGetter(null);
		}

		public static String get(IResourceGetter resourceGetter, String key) {
			String string = resourceGetter.getStringOrNull(key);
			if (string == null) {
				String message = MessageFormat.format(UtilityMessages.missingResource, key);
				System.err.println(message);
				// throw new MissingResourceException(message, IResourceGetter.class.getSimpleName(), key);
			}
			return string;
		}

	}
}
