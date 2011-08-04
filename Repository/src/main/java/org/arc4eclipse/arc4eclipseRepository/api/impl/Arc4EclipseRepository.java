package org.arc4eclipse.arc4eclipseRepository.api.impl;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.repositoryFacardConstants.RepositoryFacardConstants;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.events.ListenerList;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.future.Futures;
import org.arc4eclipse.utilities.maps.Maps;
import org.arc4eclipse.utilities.strings.Urls;

public class Arc4EclipseRepository implements IArc4EclipseRepository {
	private final static Map<String, Object> emptyParameters = Collections.<String, Object> emptyMap();

	private final IRepositoryFacard facard;
	private final IUrlGenerator urlGenerator;

	private final ListenerList<IArc4EclipseLogger> loggers = new ListenerList<IArc4EclipseLogger>();
	ListenerList<IStatusChangedListener> listeners = new ListenerList<IStatusChangedListener>();

	class CallbackForData implements IRepositoryFacardCallback {

		private final Map<String, Object> context;

		public CallbackForData(Map<String, Object> context) {
			this.context = context;
		}

		@Override
		public void process(IResponse response, Map<String, Object> data) {
			Map<String, Object> madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? data : null;
			fireResponse(response, madeData, context);
			fireStatusChangedFromResponse(response.url(), madeData, context);
		}

		@Override
		public String toString() {
			return "GotData " + context;
		}

	}

	class CallbackForModify implements IResponseCallback {
		private final Map<String, Object> context;

		public CallbackForModify(Map<String, Object> context) {
			this.context = context;
		}

		@Override
		public void process(final IResponse response) {
			fireResponse(response, null, context);
			if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode())) {
				fireRequest("RefreshingData", response.url(), emptyParameters, context);
				fireStatusChanged(response.url(), RepositoryDataItemStatus.REQUESTED, null, context);
				Future<?> future = facard.get(response.url(), new IRepositoryFacardCallback() {
					@Override
					public void process(IResponse response, Map<String, Object> data) {
						try {
							Map<String, Object> madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? data : null;
							fireResponse(response, madeData, context);
							fireStatusChangedFromResponse(response.url(), madeData, context);
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}

					}

					@Override
					public String toString() {
						return "GotAfterModify " + context;
					}
				});
				try {
					future.get();// its OK blocking here as we are in a worker thread
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			} else
				throw new RuntimeException(MessageFormat.format(RepositoryConstants.failedToChange, response.url()));
		}

		@Override
		public String toString() {
			return "Modify " + context;
		}

	}

	private void fireStatusChangedFromResponse(String url, Map<String, Object> data, Map<String, Object> context) {
		RepositoryDataItemStatus status = data == null ? RepositoryDataItemStatus.NOT_FOUND : RepositoryDataItemStatus.FOUND;
		fireStatusChanged(url, status, data, context);
	}

	private void fireStatusChanged(final String rawUrl, final RepositoryDataItemStatus status, final Map<String, Object> data, final Map<String, Object> context) {
		final String url = Urls.rip(rawUrl).resourcePath;
		listeners.fire(new ICallback<IStatusChangedListener>() {
			@Override
			public void process(IStatusChangedListener t) throws Exception {
				t.statusChanged(url, status, data, context);
			}

			@Override
			public String toString() {
				return "StatusChanged " + status + " " + rawUrl;
			}
		});
	}

	public Arc4EclipseRepository(IRepositoryFacard facard, IUrlGenerator urlGenerator, IJarDigester jarDigester) {
		this.facard = facard;
		this.urlGenerator = urlGenerator;
	}

	@Override
	public Future<?> getJarData(String jarDigest, Map<String, Object> rawContext) {
		try {
			Map<String, Object> context = new HashMap<String, Object>(rawContext);
			context.put(RepositoryConstants.entity, RepositoryConstants.entityJarData);
			context.put(RepositoryConstants.action, RepositoryConstants.actionGet);
			if (jarDigest == null || jarDigest.equals("")) {
				fireStatusChanged("", RepositoryDataItemStatus.PATH_NULL, null, context);
				return Futures.doneFuture(null);
			}
			String url = urlGenerator.forJar().apply(jarDigest);
			fireRequest("getJarData", url, emptyParameters, context);
			fireStatusChanged(url, RepositoryDataItemStatus.REQUESTED, null, context);
			return facard.get(url, new CallbackForData(context));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public Future<?> modifyJarData(String jarDigest, String name, Object value, Map<String, Object> rawContext) {
		try {
			String url = urlGenerator.forJar().apply(jarDigest);
			Map<String, Object> context = new HashMap<String, Object>(rawContext);
			context.put(RepositoryConstants.entity, RepositoryConstants.entityJarData);
			context.put(RepositoryConstants.action, RepositoryConstants.actionPost);
			Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value, RepositoryConstants.hexDigestKey, jarDigest);
			fireRequest("modifyJarData", url, parameters, context);
			return facard.post(url, parameters, new CallbackForModify(context));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public Future<?> getData(String entity, String url, Map<String, Object> rawContext) {
		Map<String, Object> context = new HashMap<String, Object>(rawContext);
		context.put(RepositoryConstants.action, RepositoryConstants.actionGet);
		context.put(RepositoryConstants.entity, entity);
		fireRequest("getData", url, emptyParameters, context);
		fireStatusChanged(url, RepositoryDataItemStatus.REQUESTED, null, context);
		return facard.get(url, new CallbackForData(context));
	}

	@Override
	public Future<?> modifyData(String entity, String url, String name, Object value, Map<String, Object> rawContext) {
		Map<String, Object> context = new HashMap<String, Object>(rawContext);
		context.put(RepositoryConstants.action, RepositoryConstants.actionPost);
		context.put(RepositoryConstants.entity, entity);
		Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value);
		fireRequest("modifyData", url, parameters, context);
		return facard.post(url, parameters, new CallbackForModify(context));
	}

	@Override
	public void addLogger(IArc4EclipseLogger logger) {
		loggers.add(logger);
	}

	@Override
	public IUrlGenerator generator() {
		return urlGenerator;
	}

	private void fireRequest(final String method, final String url, final Map<String, Object> parameters, final Map<String, Object> context) {
		loggers.fire(new ICallback<IArc4EclipseLogger>() {
			@Override
			public void process(IArc4EclipseLogger t) throws Exception {
				t.sendingRequest(method, url, parameters, context);
			}

			@Override
			public String toString() {
				return "fireRequest: " + method + " " + url + " " + context;
			}
		});
	}

	private void fireResponse(final IResponse response, final Object data, final Map<String, Object> context) {
		loggers.fire(new ICallback<IArc4EclipseLogger>() {
			@Override
			public void process(IArc4EclipseLogger t) throws Exception {
				t.receivedReply(response, data, context);
			}

			@Override
			public String toString() {
				return "fireResponse: " + response.statusCode() + " " + response.url() + " " + context;
			}
		});
	}

	@Override
	public void addStatusListener(IStatusChangedListener listener) {
		listeners.add(listener);

	}

	@Override
	public void removeStatusListener(IStatusChangedListener listener) {
		listeners.remove(listener);

	}

	@Override
	public void shutdown() {
		facard.shutdown();
	}

}
