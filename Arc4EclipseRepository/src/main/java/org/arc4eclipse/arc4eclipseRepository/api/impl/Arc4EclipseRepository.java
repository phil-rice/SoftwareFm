package org.arc4eclipse.arc4eclipseRepository.api.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IJarDigester;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.JarData;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.repositoryFacardConstants.RepositoryFacardConstants;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;

public class Arc4EclipseRepository implements IArc4EclipseRepository {
	private final static Map<String, Object> emptyParameters = Collections.<String, Object> emptyMap();

	private final IRepositoryFacard facard;
	private final IUrlGenerator urlGenerator;
	private final Map<File, String> fileToDigest = Maps.newMap();
	private final IJarDigester jarDigester;
	private final List<IArc4EclipseLogger> loggers = Collections.synchronizedList(Lists.<IArc4EclipseLogger> newList());

	abstract class CallbackForData<T> implements IRepositoryFacardCallback {
		private final IArc4EclipseCallback<T> callback;

		public CallbackForData(IArc4EclipseCallback<T> callback) {
			this.callback = callback;
		}

		@Override
		public void process(IResponse response, Map<String, Object> data) {
			try {
				T madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? makeData(data) : null;
				fireResponse(response, madeData);
				callback.process(response, madeData);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		abstract protected T makeData(Map<String, Object> data) throws Exception;

	}

	abstract class CallbackForModify<T> implements IResponseCallback {
		private final IArc4EclipseCallback<T> callback;

		public CallbackForModify(IArc4EclipseCallback<T> callback) {
			this.callback = callback;
		}

		@Override
		public void process(final IResponse response) {
			fireResponse(response, null);
			try {
				if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode())) {
					fireRequest("RefreshingData", response.url(), emptyParameters);
					facard.get(response.url(), new IRepositoryFacardCallback() {
						@Override
						public void process(IResponse response, Map<String, Object> data) {
							try {
								T madeData = RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()) ? makeData(data) : null;
								fireResponse(response, madeData);
								callback.process(response, madeData);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
				} else
					callback.process(response, null);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		abstract protected T makeData(Map<String, Object> data) throws Exception;

	}

	public Arc4EclipseRepository(IRepositoryFacard facard, IUrlGenerator urlGenerator, IJarDigester jarDigester) {
		this.facard = facard;
		this.urlGenerator = urlGenerator;
		this.jarDigester = jarDigester;
	}

	@Override
	public void getJarData(File jar, IArc4EclipseCallback<IJarData> callback) {
		try {
			String jarDigest = findJarDigest(jar);
			String url = urlGenerator.forJar(jarDigest);
			fireRequest("getJarData", url, emptyParameters);
			facard.get(url, new CallbackForData<IJarData>(callback) {
				@Override
				protected IJarData makeData(Map<String, Object> data) {
					return new JarData(data);
				}
			});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	public void modifyJarData(File jar, String name, Object value, IArc4EclipseCallback<IJarData> callback) {
		try {
			String jarDigest = findJarDigest(jar);
			String url = urlGenerator.forJar(jarDigest);
			Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value, Arc4EclipseRepositoryConstants.hexDigestKey, jarDigest);
			fireRequest("modifyJarData", url, parameters);
			facard.post(url, parameters, new CallbackForModify<IJarData>(callback) {
				@Override
				protected IJarData makeData(Map<String, Object> data) {
					return new JarData(data);
				}
			});
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public <T> void getData(String url, final IFunction1<Map<String, Object>, T> mapper, IArc4EclipseCallback<T> callback) {
		fireRequest("getData", url, emptyParameters);
		facard.get(url, new CallbackForData<T>(callback) {
			@Override
			protected T makeData(Map<String, Object> data) throws Exception {
				T to = mapper.apply(data);
				return to;
			}
		});
	}

	@Override
	public <T> void modifyData(String url, String name, Object value, final IFunction1<Map<String, Object>, T> mapper, IArc4EclipseCallback<T> callback) {
		Map<String, Object> parameters = Maps.<String, Object> makeMap(name, value);
		fireRequest("modifyData", url, parameters);
		facard.post(url, parameters, new CallbackForModify<T>(callback) {
			@Override
			protected T makeData(Map<String, Object> data) throws Exception {
				T to = mapper.apply(data);
				return to;
			}
		});

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

}
