package org.softwarefm.display.data;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;
import org.softwarefm.display.IUrlDataCallback;
import org.softwarefm.display.IUrlToData;

public class GuiDataStore implements IDataGetter {
	private final IUrlToData urlToData;
	private String mainEntity;
	private final Map<String, IUrlGenerator> urlGeneratorMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, IUrlGenerator> entityToUrlGeneratorMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, List<DependantData>> entityToDependantMap = Maps.newMap();

	private final Map<String, String> lastUrlFor = Maps.newMap();
	private final Map<String, EntityCachedData> cache = Maps.newMap();

	private final CopyOnWriteArrayList<IGuiDataListener> listeners = new CopyOnWriteArrayList<IGuiDataListener>();
	private ICallback<Throwable> onException;
	private Object rawData;

	public GuiDataStore(IUrlToData urlToData, ICallback<Throwable> onException) {
		this.urlToData = urlToData;
		this.onException = onException;
	}

	/** Really a map from Url to data for a given entity */
	static class EntityCachedData extends HashMap<String, Map<String, Object>> {

	}

	static class DependantData {
		String entity;
		String linkData;

		public DependantData(String entity, String linkData) {
			this.entity = entity;
			this.linkData = linkData;
		}
	}

	public GuiDataStore entity(String entity, String urlGeneratorId) {
		if (entityToUrlGeneratorMap.containsKey(entity))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.duplicateEntity, entity));
		mainEntity = entity;
		checkAndPut(entity, urlGeneratorId);
		return this;
	}

	public void addGuiDataListener(IGuiDataListener listener) {
		listeners.add(listener);
	}

	public void removeGuiDataListener(IGuiDataListener listener) {
		listeners.remove(listener);
	}

	public GuiDataStore dependant(String entity, String dependantEntity, String linkData, String urlGeneratorId) {
		checkEntityExists(entity);
		if (entityToUrlGeneratorMap.containsKey(dependantEntity))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.duplicateEntity, entity));
		checkAndPut(dependantEntity, urlGeneratorId);
		Maps.addToList(entityToDependantMap, entity, new DependantData(dependantEntity, linkData));
		return this;
	}

	private void checkEntityExists(String entity) {
		if (!entityToUrlGeneratorMap.containsKey(entity))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.unrecognisedEntity, entity, entityToUrlGeneratorMap.keySet()));
	}

	private void checkAndPut(String entity, String urlGeneratorId) {
		IUrlGenerator urlGenerator = urlGeneratorMap.get(urlGeneratorId);
		if (urlGenerator == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.unrecognisedUrlGenerator, urlGeneratorId, urlGeneratorMap.keySet()));
		entityToUrlGeneratorMap.put(entity, urlGenerator);
	}

	public GuiDataStore urlGenerator(String key, IUrlGenerator jarUrlGenerator) {
		if (urlGeneratorMap.containsKey(key))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.duplicateUrlGenerator, key));
		urlGeneratorMap.put(key, jarUrlGenerator);
		return this;
	}

	public void processData(Object data, Map<String, Object> context) {
		this.rawData = data;
		processOneData(mainEntity, data, context);
	}

	public void processOneData(String entity, Object data, Map<String, Object> context) {
		IUrlGenerator urlGenerator = entityToUrlGeneratorMap.get(entity);
		if (urlGenerator == null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.unrecognisedUrlGenerator, entity, entityToUrlGeneratorMap.keySet()));
		String url = urlGenerator.findUrlFor(entity, data);
		final EntityCachedData entityCachedData = getFromCache(entity);

		IUrlDataCallback callback = new IUrlDataCallback() {
			@Override
			public void processData(String entity, String url, Map<String, Object> context, Map<String, Object> data) {
				fireListeners(entity, url, context, data);
				entityCachedData.put(url, data);
				lastUrlFor.put(entity, url);
				for (DependantData dependantData : Maps.getOrEmptyList(entityToDependantMap, entity)) {
					Object linkObject = data.get(dependantData.linkData);
					processOneData(dependantData.entity, linkObject, context);
				}
			}

		};
		if (entityCachedData.containsKey(url)) {
			// fireListeners(entity, url, context, entityCachedData.get(url));
			callback.processData(entity, url, context, entityCachedData.get(url));
		} else
			urlToData.getData(entity, url, context, callback);
	}

	private EntityCachedData getFromCache(String entity) {
		final EntityCachedData entityCachedData = Maps.findOrCreate(cache, entity, new Callable<EntityCachedData>() {
			@Override
			public EntityCachedData call() throws Exception {
				return new EntityCachedData();
			}
		});
		return entityCachedData;
	}

	private void fireListeners(String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		for (IGuiDataListener listener : listeners)
			try {
				listener.data(entity, url, context, data);
			} catch (Throwable t) {
				try {
					onException.process(t);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}

	}

	@Override
	public Object getDataFor(String path) {
		int index = path.indexOf('.');
		if (index == -1)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.illegalPath, path));
		String entity = path.substring(0, index);
		String key = path.substring(index + 1);
		if (entity.equals("raw"))
			return getRawData(key);
		Object result = getDataFor(entity, key);
		System.out.println("GetDataFor " + entity + ", " + key + " -> " + result);
		return result;
	}

	@SuppressWarnings("rawtypes")
	private Object getRawData(String key) {
		if (rawData == null)
			return null;
		if (!(rawData instanceof Map))
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.expectedAMap, rawData, rawData.getClass()));
		return ((Map) rawData).get(key);
	}

	private Object getDataFor(String entity, String key) {
		checkEntityExists(entity);
		EntityCachedData entityCachedData = cache.get(entity);
		if (entityCachedData == null)
			return null;
		String url = lastUrlFor(entity);
		Map<String, Object> map = entityCachedData.get(url);
		if (map == null)
			return null;
		return map.get(key);
	}

	public String lastUrlFor(String entity) {
		return lastUrlFor.get(entity);
	}

	public void forceData(String url, String entity, Map<String, Object> data, Map<String, Object> context) {
		lastUrlFor.put(entity, url);
		final EntityCachedData entityCachedData = getFromCache(entity);
		entityCachedData.put(url, data);
		fireListeners(entity, url, context, data);
	}

	@Override
	public Object getLastRawData() {
		return rawData;
	}

	@Override
	public void setRawData(Object rawData) {
		this.rawData = rawData;

	}

}
