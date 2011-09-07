package org.softwareFm.repository.api.impl;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.jdtBinding.api.IJarDigester;
import org.softwareFm.repository.api.IRepositoryStatusListener;
import org.softwareFm.repository.api.ISoftwareFmLogger;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.events.ListenerList;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Urls;

public class SoftwareFmRepository implements ISoftwareFmRepository {
	private final static Map<String, Object> emptyParameters = Collections.<String, Object> emptyMap();

	private final IRepositoryFacard facard;

	private final ListenerList<ISoftwareFmLogger> loggers = new ListenerList<ISoftwareFmLogger>();
	ListenerList<IRepositoryStatusListener> listeners = new ListenerList<IRepositoryStatusListener>();

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
		listeners.fire(new ICallback<IRepositoryStatusListener>() {
			@Override
			public void process(IRepositoryStatusListener t) throws Exception {
				t.statusChanged(url, status, data, context);
			}

			@Override
			public String toString() {
				return "StatusChanged " + status + " " + rawUrl;
			}
		});
	}

	public SoftwareFmRepository(IRepositoryFacard facard, IJarDigester jarDigester) {
		this.facard = facard;
	}

	// @Override
	// public Future<?> getJarData(String jarDigest, Map<String, Object> rawContext) {
	// try {
	// Map<String, Object> context = new HashMap<String, Object>(rawContext);
	// context.put(RepositoryConstants.entity, RepositoryConstants.entityJar);
	// context.put(RepositoryConstants.action, RepositoryConstants.actionGet);
	// if (jarDigest == null || jarDigest.equals("")) {
	// fireStatusChanged("", RepositoryDataItemStatus.PATH_NULL, null, context);
	// return Futures.doneFuture(null);
	// }
	// String url = urlGeneratorMap.forJar().apply(jarDigest);
	// fireRequest("getJarData", url, emptyParameters, context);
	// fireStatusChanged(url, RepositoryDataItemStatus.REQUESTED, null, context);
	// return facard.get(url, new CallbackForData(context));
	// } catch (Exception e) {
	// throw WrappedException.wrap(e);
	// }
	//
	// }
	//
	// @Override
	// public Future<?> modifyJarData(String jarDigest, String name, Object value, Map<String, Object> rawContext) {
	// try {
	// String url = urlGeneratorMap.forJar().apply(jarDigest);
	// Map<String, Object> context = new HashMap<String, Object>(rawContext);
	// context.put(RepositoryConstants.entity, RepositoryConstants.entityJar);
	// context.put(RepositoryConstants.action, RepositoryConstants.actionPost);
	// Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value, RepositoryConstants.hexDigestKey, jarDigest);
	// fireRequest("modifyJarData", url, parameters, context);
	// return facard.post(url, parameters, new CallbackForModify(context));
	// } catch (Exception e) {
	// throw WrappedException.wrap(e);
	// }
	// }

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
	public void addLogger(ISoftwareFmLogger logger) {
		loggers.add(logger);
	}

	private void fireRequest(final String method, final String url, final Map<String, Object> parameters, final Map<String, Object> context) {
		loggers.fire(new ICallback<ISoftwareFmLogger>() {
			@Override
			public void process(ISoftwareFmLogger t) throws Exception {
				t.sendingRequest(method, url, parameters, context);
			}

			@Override
			public String toString() {
				return "fireRequest: " + method + " " + url + " " + context;
			}
		});
	}

	private void fireResponse(final IResponse response, final Object data, final Map<String, Object> context) {
		loggers.fire(new ICallback<ISoftwareFmLogger>() {
			@Override
			public void process(ISoftwareFmLogger t) throws Exception {
				t.receivedReply(response, data, context);
			}

			@Override
			public String toString() {
				return "fireResponse: " + response.statusCode() + " " + response.url() + " " + context;
			}
		});
	}

	@Override
	public void addStatusListener(IRepositoryStatusListener listener) {
		listeners.add(listener);

	}

	@Override
	public void removeStatusListener(IRepositoryStatusListener listener) {
		listeners.remove(listener);

	}

	@Override
	public void shutdown() {
		facard.shutdown();
	}

	@Override
	public void notifyListenersThereIsNoData(String entity, Map<String, Object> context) {
		Map<String, Object> fullContext = Maps.copyMap(context);
		fullContext.put(RepositoryConstants.entity, entity);
		fullContext.put(RepositoryConstants.action, RepositoryConstants.actionNotifyNoData);
		fireStatusChanged(null, RepositoryDataItemStatus.NOT_FOUND, null, fullContext);
	}
}
