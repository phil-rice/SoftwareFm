package org.arc4eclipse.arc4eclipseRepository.api.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IJarDigester;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.repositoryFacardConstants.RepositoryFacardConstants;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.future.Futures;
import org.arc4eclipse.utilities.maps.Maps;

public class Arc4EclipseRepository implements IArc4EclipseRepository {
	private final static Map<String, Object> emptyParameters = Collections.<String, Object> emptyMap();

	private final IRepositoryFacard facard;
	private final IUrlGenerator urlGenerator;
	private final Map<File, String> fileToDigest = Maps.newMap();
	private final IJarDigester jarDigester;

	private final Collection<IArc4EclipseLogger> loggers = Collections.synchronizedList(Lists.<IArc4EclipseLogger> newList());
	private final Map<Class<?>, Collection<IStatusChangedListener<IRepositoryDataItem>>> listeners = Collections.synchronizedMap(Maps.<Class<?>, Collection<IStatusChangedListener<IRepositoryDataItem>>> newMap());
	private final Map<Class<?>, IFunction1<Map<String, Object>, IRepositoryDataItem>> mappers;

	class CallbackForData<T extends IRepositoryDataItem> implements IRepositoryFacardCallback {

		private final Class<T> clazz;

		public CallbackForData(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public void process(IResponse response, Map<String, Object> data) {
			T madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? makeData(clazz, data) : null;
			fireResponse(response, madeData);
			fireStatusChangedFromResponse(response.url(), clazz, madeData);
		}

	}

	class CallbackForModify<T extends IRepositoryDataItem> implements IResponseCallback {
		private final Class<T> clazz;

		public CallbackForModify(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public void process(final IResponse response) {
			fireResponse(response, null);
			if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode())) {
				fireRequest("RefreshingData", response.url(), emptyParameters);
				fireStatusChanged(response.url(), clazz, RepositoryDataItemStatus.REQUESTED, null);
				Future<?> future = facard.get(response.url(), new IRepositoryFacardCallback() {
					@Override
					public void process(IResponse response, Map<String, Object> data) {
						try {
							T madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? makeData(clazz, data) : null;
							fireResponse(response, madeData);
							fireStatusChangedFromResponse(response.url(), clazz, madeData);
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}
					}
				});
				try {
					future.get();// its OK blocking here as we are in a worker thread
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			} else
				throw new RuntimeException(MessageFormat.format(Arc4EclipseRepositoryConstants.failedToChange, clazz.getSimpleName()));
		}

	}

	@SuppressWarnings("unchecked")
	private <T extends IRepositoryDataItem> T makeData(Class<T> Clazz, Map<String, Object> data) {
		try {
			IFunction1<Map<String, Object>, IRepositoryDataItem> mapper = getMapperFor(Clazz);
			return (T) mapper.apply(data);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	private <T> IFunction1<Map<String, Object>, IRepositoryDataItem> getMapperFor(Class<T> clazz) {
		for (Entry<Class<?>, IFunction1<Map<String, Object>, IRepositoryDataItem>> entry : mappers.entrySet())
			if (entry.getKey().isAssignableFrom(clazz))
				return entry.getValue();
		throw new IllegalArgumentException(MessageFormat.format(Arc4EclipseRepositoryConstants.cannotFindMapperFor, clazz));
	}

	private <T extends IRepositoryDataItem> void fireStatusChangedFromResponse(String url, Class<T> clazz, T data) {
		RepositoryDataItemStatus status = data == null ? RepositoryDataItemStatus.NOT_FOUND : RepositoryDataItemStatus.FOUND;
		fireStatusChanged(url, clazz, status, data);
	}

	private <T extends IRepositoryDataItem> void fireStatusChanged(String url, Class<T> clazz, RepositoryDataItemStatus status, T data) {
		try {
			Collection<IStatusChangedListener<IRepositoryDataItem>> collection = findListenersFor(clazz);
			if (collection != null)
				for (IStatusChangedListener<IRepositoryDataItem> listener : collection)
					listener.statusChanged(url, clazz, status, data);
			if (clazz != IRepositoryDataItem.class)
				fireStatusChanged(url, IRepositoryDataItem.class, status, data);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private <T> Collection<IStatusChangedListener<IRepositoryDataItem>> findListenersFor(Class<T> clazz) {
		return listeners.get(clazz);
	}

	public Arc4EclipseRepository(IRepositoryFacard facard, IUrlGenerator urlGenerator, IJarDigester jarDigester) {
		this.facard = facard;
		this.urlGenerator = urlGenerator;
		this.jarDigester = jarDigester;
		mappers = Collections.synchronizedMap(Maps.<Class<?>, IFunction1<Map<String, Object>, IRepositoryDataItem>> makeMap(//
				IJarData.class, IArc4EclipseRepository.Utils.jarData(),//
				IOrganisationData.class, IArc4EclipseRepository.Utils.organisationData(),//
				IProjectData.class, IArc4EclipseRepository.Utils.projectData()));
	}

	@Override
	public Future<?> getJarData(File jar) {
		try {
			if (jar == null) {
				fireStatusChanged("", IRepositoryDataItem.class, RepositoryDataItemStatus.PATH_NULL, null);
				return Futures.doneFuture(null);
			}
			String jarDigest = findJarDigest(jar);
			String url = urlGenerator.forJar().apply(jarDigest);
			fireRequest("getJarData", url, emptyParameters);
			fireStatusChanged(url, IJarData.class, RepositoryDataItemStatus.REQUESTED, null);
			return facard.get(url, new CallbackForData<IJarData>(IJarData.class));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public Future<?> modifyJarData(File jar, String name, Object value) {
		try {
			String jarDigest = findJarDigest(jar);
			String url = urlGenerator.forJar().apply(jarDigest);
			Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value, Arc4EclipseRepositoryConstants.hexDigestKey, jarDigest);
			fireRequest("modifyJarData", url, parameters);
			return facard.post(url, parameters, new CallbackForModify<IJarData>(IJarData.class));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public <T extends IRepositoryDataItem> Future<?> getData(String url, Class<T> clazz) {
		fireRequest("getData", url, emptyParameters);
		fireStatusChanged(url, clazz, RepositoryDataItemStatus.REQUESTED, null);
		return facard.get(url, new CallbackForData<T>(clazz));
	}

	@Override
	public <T extends IRepositoryDataItem> Future<?> modifyData(String url, String name, Object value, Class<T> clazz) {
		Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value);
		fireRequest("modifyData", url, parameters);
		return facard.post(url, parameters, new CallbackForModify<T>(clazz));

	}

	@Override
	public void cleanCache() {
		fileToDigest.clear();
	}

	private String findJarDigest(final File jar) throws Exception {
		return Maps.findOrCreate(fileToDigest, jar, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return jarDigester.apply(jar);
			}
		});
	}

	@Override
	public void addLogger(IArc4EclipseLogger logger) {
		loggers.add(logger);
	}

	@Override
	public IUrlGenerator generator() {
		return urlGenerator;
	}

	private void fireRequest(String method, String url, Map<String, Object> parameters) {
		for (IArc4EclipseLogger logger : loggers)
			logger.sendingRequest(method, url, parameters);
	}

	private void fireResponse(IResponse response, Object data) {
		for (IArc4EclipseLogger logger : loggers)
			logger.receivedReply(response, data);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends IRepositoryDataItem> void addStatusListener(Class<T> clazz, IStatusChangedListener<T> listener) {
		Maps.addToCollection((Map) listeners, ArrayList.class, clazz, listener);

	}

	@Override
	public void shutdown() {
		facard.shutdown();
	}

}
