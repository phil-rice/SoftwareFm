package org.arc4eclipse.utilities.json;

import java.text.MessageFormat;
import java.util.Map;

import org.arc4eclipse.utilities.constants.UtilityConstants;
import org.arc4eclipse.utilities.constants.UtilityMessages;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class Json {

	public static Map<String, Object> mapFromString(String jsonObject) {
		return makeMap(jsonObject);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, Object> makeMap(String jsonString) {
		if (jsonString instanceof String) {
			Object result = parse(jsonString);
			if (result instanceof Map)
				return (Map) result;
		}
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
}
