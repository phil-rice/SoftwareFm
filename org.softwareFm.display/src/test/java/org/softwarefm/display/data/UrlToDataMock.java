package org.softwarefm.display.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwarefm.display.IUrlDataCallback;
import org.softwarefm.display.IUrlToData;

public class UrlToDataMock implements IUrlToData {

	private final Map<String, AtomicInteger> count = Maps.newMap();
	public List<String> entities = Lists.newList();
	public List<String> urls = Lists.newList();
	public List<Map<String, Object>> contexts = Lists.newList();

	private final Map<String, List<Map<String, Object>>> mockData;

	public UrlToDataMock(Object... entitiesAndListOfMaps) {
		this(Maps.<String, List<Map<String, Object>>> makeLinkedMap(entitiesAndListOfMaps));

	}

	public UrlToDataMock(Map<String, List<Map<String, Object>>> mockData) {
		this.mockData = mockData;
	}

	@Override
	public void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback) {
		entities.add(entity);
		urls.add(url);
		contexts.add(context);
		mockData.get(entity);
		AtomicInteger integer = Maps.findOrCreate(count, entity, new Callable<AtomicInteger>() {
			@Override
			public AtomicInteger call() throws Exception {
				return new AtomicInteger();
			}
		});
		List<Map<String, Object>> list = mockData.get(entity);
		Map<String, Object> data = list.get(integer.getAndIncrement());
		callback.processData(entity, url, context, data);
	}
}
