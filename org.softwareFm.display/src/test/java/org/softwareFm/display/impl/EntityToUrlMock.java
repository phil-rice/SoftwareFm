package org.softwareFm.display.impl;

import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class EntityToUrlMock implements IFunction1<String, String> {

	private final Map<String, String> map;
	public List<String> entities = Lists.newList();

	public EntityToUrlMock(String... namesAndValues) {
		map = Maps.makeMap((Object[])namesAndValues);
	}

	@Override
	public String apply(String entity) throws Exception {
		entities.add(entity);
		return map.get(entity);
	}

}
