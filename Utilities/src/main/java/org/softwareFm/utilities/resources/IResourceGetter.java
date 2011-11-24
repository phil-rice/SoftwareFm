package org.softwareFm.utilities.resources;

import java.text.MessageFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.softwareFm.utilities.constants.UtilityMessages;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public interface IResourceGetter {

	String getStringOrNull(String fullKey);

	IResourceGetter with(IResourceGetter getter);

	IResourceGetter with(Class<?> anchorClass, String propertyName);

	IResourceGetter with(ResourceBundle bundle);

	public static class Utils {
		public static IResourceGetter noResources() {
			return new ResourceGetter(null);
		}

		public static String getOrException(IResourceGetter resourceGetter, String key) {
			String string = resourceGetter.getStringOrNull(key);
			if (string == null) {
				String message = MessageFormat.format(UtilityMessages.missingResource, key);
				throw new MissingResourceException(message, IResourceGetter.class.getSimpleName(), key);
			}
			return string;

		}

		public static <T> String getOrException(IFunction1<T, IResourceGetter> fn, T t, String key) {
			IResourceGetter resourceGetter = Functions.call(fn, t);
			String string = resourceGetter.getStringOrNull(key);
			if (string == null) {
				String message = MessageFormat.format(UtilityMessages.missingResource, key);
				throw new MissingResourceException(message, IResourceGetter.class.getSimpleName(), key);
			}
			return string;

		}

		public static String get(IResourceGetter resourceGetter, String key) {
			String string = resourceGetter.getStringOrNull(key);
			if (string == null) {
				String message = MessageFormat.format(UtilityMessages.missingResource, key);
				System.err.println(message);
			}
			return string;
		}

		public static <T> String get(IFunction1<T, IResourceGetter> fn, T t, String key) {
			IResourceGetter resourceGetter = Functions.call(fn, t);
			String string = resourceGetter.getStringOrNull(key);
			if (string == null) {
				String message = MessageFormat.format(UtilityMessages.missingResource, key);
				System.err.println(message);
			}
			return string;
		}

		public static <T>int getIntegerOrException(IFunction1<T, IResourceGetter>resourceGetterFn, T t, String indentKey) {
			String raw = getOrException(resourceGetterFn, t, indentKey);
			return Integer.parseInt(raw);
			
		}
		public static int getIntegerOrException(IResourceGetter resourceGetter, String indentKey) {
			String raw = getOrException(resourceGetter, indentKey);
			return Integer.parseInt(raw);
		}

		public static <T> String getOrNull(IFunction1<T, IResourceGetter> resourceGetterFn, T cardType, String key) {
			IResourceGetter resourceGetter = Functions.call(resourceGetterFn, cardType);
			return resourceGetter.getStringOrNull(key);
		}
		public static <T> String getOr(IFunction1<T, IResourceGetter> resourceGetterFn, T cardType, String key, String defaultValue) {
			IResourceGetter resourceGetter = Functions.call(resourceGetterFn, cardType);
			String result = resourceGetter.getStringOrNull(key);
			return result == null? defaultValue : result;
		}
		
		public static IFunction1<String, IResourceGetter> mock(final IResourceGetter defaultValue, final Object...namesAndResourceGetters){
			return new IFunction1<String, IResourceGetter>() {
				private final Map<String, IResourceGetter> map = Maps.makeMap(namesAndResourceGetters);
				@Override
				public IResourceGetter apply(String from) throws Exception {
					IResourceGetter result = map.get(from);
					return result == null?defaultValue:result.with(defaultValue);
				}
			};
		}

	}
}
