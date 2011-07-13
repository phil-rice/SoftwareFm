package org.arc4eclipse.repositoryFacard.impl;

import java.text.MessageFormat;
import java.util.Map;

import org.arc4eclipse.repositoryFacard.IAspectToParameters;
import org.arc4eclipse.repositoryFacard.constants.RepositoryFacardConstants;
import org.arc4eclipse.utilities.functions.Functions;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class AspectToParameters<Thing, Aspect> implements IAspectToParameters<Thing, Aspect, Map<Object, Object>> {

	@Override
	public String[] makeParameters(Thing thing, Aspect aspect, Map<Object, Object> data) {
		final Map<Object, String> jsonValueMap = Maps.mapTheMap(data, Functions.identity(), new IFunction1<Object, String>() {
			@Override
			public String apply(Object from) throws Exception {
				return JSONValue.toJSONString(from);
			}
		});
		String[] parameters = Maps.toParameters(jsonValueMap, String.class);
		return parameters;
	}

	@Override
	public Map<Object, Object> makeFrom(String string) {
		Map<Object, Object> map = makeMap(string);
		return map;
	}

	@Override
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
