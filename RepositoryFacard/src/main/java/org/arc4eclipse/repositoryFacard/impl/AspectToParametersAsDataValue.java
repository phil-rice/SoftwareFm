package org.arc4eclipse.repositoryFacard.impl;

import java.text.MessageFormat;
import java.util.Map;

import org.arc4eclipse.repositoryFacard.IAspectToParameters;
import org.arc4eclipse.repositoryFacard.constants.RepositoryFacardConstants;
import org.arc4eclipse.utilities.maps.Maps;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class AspectToParametersAsDataValue<Thing, Aspect> implements IAspectToParameters<Thing, Aspect, Map<Object, Object>> {

	
	public String[] makeParameters(Thing thing, Aspect aspect, Map<Object, Object> data) {
		return new String[] { RepositoryFacardConstants.dataKey, JSONValue.toJSONString(data) };
	}

	
	public Map<Object, Object> makeFrom(String string) {
		Map<Object, Object> raw = makeMap(string);
		Object data = raw.get(RepositoryFacardConstants.dataKey);
		Map<Object, Object> dataMap = makeMap(data);
		return dataMap;
	}

	
	public Map<Object, Object> makeEmpty() {
		return Maps.newMap();
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> makeMap(Object object) {
		if (object instanceof Map)
			return (Map<Object, Object>) object;
		if (object instanceof String) {
			try {
				Object result = JSONValue.parseWithException((String) object);
				if (result instanceof Map)
					return (Map<Object, Object>) result;
				else
					throw new IllegalArgumentException(MessageFormat.format(RepositoryFacardConstants.notAMap, result.getClass(), result));
			} catch (ParseException e) {
				throw new IllegalArgumentException(MessageFormat.format(RepositoryFacardConstants.notAMap, object.getClass(), object), e);
			}
		}
		throw new IllegalArgumentException(MessageFormat.format(RepositoryFacardConstants.notAMap, object.getClass(), object));
	}

}
