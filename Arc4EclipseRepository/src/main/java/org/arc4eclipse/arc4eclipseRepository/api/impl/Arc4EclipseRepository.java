package org.arc4eclipse.arc4eclipseRepository.api.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Future;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.repositoryFacardConstants.RepositoryFacardConstants;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.future.Futures;
import org.arc4eclipse.utilities.maps.Maps;

public class Arc4EclipseRepository implements IArc4EclipseRepository {
	private final static Map<String, Object> emptyParameters = Collections.<String, Object> emptyMap();

	private final IRepositoryFacard facard;
	private final IUrlGenerator urlGenerator;

	private final Collection<IArc4EclipseLogger> loggers = Collections.synchronizedList(Lists.<IArc4EclipseLogger> newList());
	Collection<IStatusChangedListener> listeners = Collections.synchronizedList(Lists.<IStatusChangedListener> newList());

	class CallbackForData implements IRepositoryFacardCallback {

		@Override
		public void process(IResponse response, Map<String, Object> data) {
			Map<String, Object> madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? data : null;
			fireResponse(response, madeData);
			fireStatusChangedFromResponse(response.url(), madeData);
		}

	}

	class CallbackForModify implements IResponseCallback {
		@Override
		public void process(final IResponse response) {
			fireResponse(response, null);
			if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode())) {
				fireRequest("RefreshingData", response.url(), emptyParameters);
				fireStatusChanged(response.url(), RepositoryDataItemStatus.REQUESTED, null);
				Future<?> future = facard.get(response.url(), new IRepositoryFacardCallback() {
					@Override
					public void process(IResponse response, Map<String, Object> data) {
						try {
							Map<String, Object> madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? data : null;
							fireResponse(response, madeData);
							fireStatusChangedFromResponse(response.url(), madeData);
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
				throw new RuntimeException(MessageFormat.format(Arc4EclipseRepositoryConstants.failedToChange, response.url()));
		}

	}

	private void fireStatusChangedFromResponse(String url, Map<String, Object> data) {
		RepositoryDataItemStatus status = data == null ? RepositoryDataItemStatus.NOT_FOUND : RepositoryDataItemStatus.FOUND;
		fireStatusChanged(url, status, data);
	}

	private void fireStatusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> data) {
		try {
			for (IStatusChangedListener listener : listeners)
				listener.statusChanged(url, status, data);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public Arc4EclipseRepository(IRepositoryFacard facard, IUrlGenerator urlGenerator, IJarDigester jarDigester) {
		this.facard = facard;
		this.urlGenerator = urlGenerator;
	}

	@Override
	public Future<?> getJarData(String jarDigest) {
		try {
			if (jarDigest == null) {
				fireStatusChanged("", RepositoryDataItemStatus.PATH_NULL, null);
				return Futures.doneFuture(null);
			}
			String url = urlGenerator.forJar().apply(jarDigest);
			fireRequest("getJarData", url, emptyParameters);
			fireStatusChanged(url, RepositoryDataItemStatus.REQUESTED, null);
			return facard.get(url, new CallbackForData());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public Future<?> modifyJarData(String jarDigest, String name, Object value) {
		try {
			String url = urlGenerator.forJar().apply(jarDigest);
			Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value, Arc4EclipseRepositoryConstants.hexDigestKey, jarDigest);
			fireRequest("modifyJarData", url, parameters);
			return facard.post(url, parameters, new CallbackForModify());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public Future<?> getData(String url) {
		fireRequest("getData", url, emptyParameters);
		fireStatusChanged(url, RepositoryDataItemStatus.REQUESTED, null);
		return facard.get(url, new CallbackForData());
	}

	@Override
	public Future<?> modifyData(String url, String name, Object value) {
		Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value);
		fireRequest("modifyData", url, parameters);
		return facard.post(url, parameters, new CallbackForModify());
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
	public void addStatusListener(IStatusChangedListener listener) {
		listeners.add(listener);

	}

	@Override
	public void shutdown() {
		facard.shutdown();
	}

}
