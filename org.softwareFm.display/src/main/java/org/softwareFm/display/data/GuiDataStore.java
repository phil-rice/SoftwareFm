package org.softwareFm.display.data;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.display.IUrlDataCallback;
import org.softwareFm.display.IUrlToData;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.ContainsAndValue;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class GuiDataStore implements IDataGetter {
	private final IUrlToData urlToData;
	private String mainEntity;
	private Object lock = new Object();
	private final Map<String, IUrlGenerator> urlGeneratorMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, IUrlGenerator> entityToUrlGeneratorMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, List<DependantData>> entityToDependantMap = Maps.newMap();

	private final Map<String, String> lastUrlFor = Maps.newMap();
	private final Map<String, EntityCachedData> cache = Maps.newMap();

	private final CopyOnWriteArrayList<IGuiDataListener> listeners = new CopyOnWriteArrayList<IGuiDataListener>();
	private ICallback<Throwable> onException;
	private Object rawData;
	private final IResourceGetter resourceGetter;

	public GuiDataStore(IUrlToData urlToData, IResourceGetter resourceGetter, ICallback<Throwable> onException) {
		this.urlToData = urlToData;
		this.resourceGetter = resourceGetter;
		this.onException = onException;
	}

	/** Really a map from Url to data for a given entity */
	static class EntityCachedData extends HashMap<String, Map<String, Object>> {

	}

	public static class DependantData {
		public String entity;
		public String linkData;

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
				synchronized (lock) {
					lastUrlFor.put(entity, url);
					entityCachedData.put(url, data);
				}
				// TODO is there some unpleasant race condition here...Probably need to rewrite
				for (DependantData dependantData : Maps.getOrEmptyList(entityToDependantMap, entity)) {
					Object linkObject = data == null ? null : data.get(dependantData.linkData);
					processOneData(dependantData.entity, linkObject, context);
				}
			}

		};
		if (url == null)
			callback.processData(entity, url, context, Collections.<String, Object> emptyMap());
		else {
			ContainsAndValue<Map<String, Object>> containsAndValue = Maps.containsAndValue(lock, entityCachedData, url);
			if (containsAndValue.contained) {
				callback.processData(entity, url, context, containsAndValue.value);
			} else
				urlToData.getData(entity, url, context, callback);
		}
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
	public Object getDataFor(String pathOrKey) {
		if (pathOrKey.startsWith("data."))
			synchronized (lock) {
				String path = pathOrKey.substring(5);
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
		else
			return Resources.getOrException(resourceGetter, pathOrKey);
	}

	@SuppressWarnings("rawtypes")
	private Object getRawData(String key) {
		synchronized (lock) {
			if (rawData == null)
				return null;
			if (!(rawData instanceof Map))
				throw new IllegalStateException(MessageFormat.format(DisplayConstants.expectedAMap, rawData, rawData.getClass()));
			return ((Map) rawData).get(key);
		}
	}

	private Object getDataFor(String entity, String key) {
		synchronized (lock) {
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
	}

	public String lastUrlFor(String entity) {
		synchronized (lock) {
			return lastUrlFor.get(entity);
		}
	}

	@Override
	public ActionData getActionDataFor(List<String> params) {
		synchronized (lock) {
			return new ActionData(lastUrlFor, params, Lists.map(params, new IFunction1<String, Object>() {
				@Override
				public Object apply(String from) throws Exception {
					return getDataFor(from);
				}
			}));
		}
	}

	public void forceData(String url, String entity, Map<String, Object> data, Map<String, Object> context) {
		synchronized (lock) {
			lastUrlFor.put(entity, url);
			final EntityCachedData entityCachedData = getFromCache(entity);
			entityCachedData.put(url, data);
			fireListeners(entity, url, context, data);
		}
	}

	@Override
	public Object getLastRawData() {
		return rawData;
	}

	@Override
	public void setRawData(Object rawData) {
		this.rawData = rawData;

	}

	public Map<String, IUrlGenerator> getUrlGeneratorMap() {
		return Collections.unmodifiableMap(urlGeneratorMap);
	}

	public Map<String, List<DependantData>> getEntityToDependantMap() {
		return Collections.unmodifiableMap(entityToDependantMap);
	}

	public String getMainEntity() {
		return mainEntity;
	}

	@Override
	public void clearCache(String url, String entity, String attribute) {
		final EntityCachedData entityCachedData = getFromCache(entity);
		entityCachedData.remove(url);
	}


}
