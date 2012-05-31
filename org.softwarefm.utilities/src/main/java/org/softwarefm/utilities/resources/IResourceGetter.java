/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.resources;

import java.text.MessageFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.softwarefm.utilities.constants.UtilityMessages;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.Maps;

public interface IResourceGetter {

	String getStringOrNull(String fullKey);

	IResourceGetter with(IResourceGetter getter);

	IResourceGetter with(Class<?> anchorClass, String propertyName);

	IResourceGetter with(ResourceBundle bundle);

	abstract public static class Utils {
		public static IResourceGetter noResources() {
			return new ResourceGetter(null);
		}

		public static IResourceGetter resourceGetter(Class<?> anchorClass, String path) {
			return new ResourceGetter(null).with(anchorClass, path);
		}

		public static String getOrException(IResourceGetter resourceGetter, String key) {
			String string = resourceGetter.getStringOrNull(key);
			if (string == null) {
				String message = MessageFormat.format(UtilityMessages.missingResource, key);
				throw new MissingResourceException(message, IResourceGetter.class.getSimpleName(), key);
			}
			return string;

		}

		public static String getMessageOrException(IResourceGetter resourceGetter, String key, Object[] args) {
			String pattern = getOrException(resourceGetter, key);
			String message = MessageFormat.format(pattern, args);
			return message;
		}

		public static <T> String getMessageOrException(IFunction1<T, IResourceGetter> fn, T t, String key, Object... args) {
			String pattern = getOrException(fn, t, key);
			String message = MessageFormat.format(pattern, args);
			return message;

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
			return string;
		}

		public static <T> String get(IFunction1<T, IResourceGetter> fn, T t, String key) {
			IResourceGetter resourceGetter = Functions.call(fn, t);
			String string = resourceGetter.getStringOrNull(key);
			return string;
		}

		public static <T> int getIntegerOrException(IFunction1<T, IResourceGetter> resourceGetterFn, T t, String indentKey) {
			String raw = getOrException(resourceGetterFn, t, indentKey);
			try {
				return Integer.parseInt(raw);
			} catch (NumberFormatException e) {
				throw e;
			}

		}

		public static int getIntegerOrException(IResourceGetter resourceGetter, String indentKey) {
			String raw = getOrException(resourceGetter, indentKey);
			return Integer.parseInt(raw);
		}

		public static <T> String getOrNull(IFunction1<T, IResourceGetter> resourceGetterFn, T cardType, String key) {
			try {
				IResourceGetter resourceGetter = Functions.call(resourceGetterFn, cardType);
				return resourceGetter.getStringOrNull(key);
			} catch (Exception e) {
				return null;
			}
		}

		public static <T> String getOr(IFunction1<T, IResourceGetter> resourceGetterFn, T cardType, String key, String defaultValue) {
			IResourceGetter resourceGetter = Functions.call(resourceGetterFn, cardType);
			String result = resourceGetter.getStringOrNull(key);
			return result == null ? defaultValue : result;
		}

		public static IFunction1<String, IResourceGetter> mock(final IResourceGetter defaultValue, final Object... namesAndResourceGetters) {
			return new IFunction1<String, IResourceGetter>() {
				private final Map<String, IResourceGetter> map = Maps.makeMap(namesAndResourceGetters);

				@Override
				public IResourceGetter apply(String from) throws Exception {
					IResourceGetter result = map.get(from);
					return result == null ? defaultValue : result.with(defaultValue);
				}
			};
		}

	}
}