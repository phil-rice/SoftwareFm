/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.json;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.UtilityConstants;
import org.softwareFm.common.constants.UtilityMessages;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;

public class Json {

	public static Map<String, Object> mapFromString(String jsonObject) {
		return makeMap(jsonObject);
	}

	public static Map<String, Object> mapFromEncryptedFile(File file, String key) {
		String text = Files.getText(file);
		String decrypted = Crypto.aesDecrypt(key, text);
		Map<String, Object> result = mapFromString(decrypted);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, Object> makeMap(String jsonString) {
		Object result = parse(jsonString);
		if (result instanceof Map)
			return (Map) result;
		throw new IllegalArgumentException(MessageFormat.format(UtilityConstants.notAMap, jsonString.getClass(), jsonString));
	}

	public static Object parse(Object jsonObject) {
		try {
			Object result = JSONValue.parseWithException((String) jsonObject);
			return result;
		} catch (ParseException e) {
			throw new JsonParseException(MessageFormat.format(UtilityMessages.errorParsingJson, jsonObject.getClass(), jsonObject), e);
		}
	}

	public static String toString(Object from) {
		return JSONValue.toJSONString(from);
	}

	public static String mapToString(Object... namesAndValues) {
		return toString(Maps.stringObjectLinkedMap(namesAndValues));
	}

	public static Map<String, Object> parseFile(File file) {
		String text = Files.getText(file);
		return Json.mapFromString(text);
	}

	public static IFunction1<String, Map<String, Object>> decryptAndMapMakeFn(final String key) {
		return new IFunction1<String, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(String from) throws Exception {
				String raw = key == null ? from : Crypto.aesDecrypt(key, from);
				return mapFromString(raw);
			}
		};
	}
}