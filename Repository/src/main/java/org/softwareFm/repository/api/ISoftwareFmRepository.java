package org.softwareFm.repository.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.jdtBinding.api.IJarDigester;
import org.softwareFm.repository.api.impl.SoftwareFmRepository;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;

public interface ISoftwareFmRepository {

	Future<?> getData(String entity, String url, Map<String, Object> context);

	Future<?> modifyData(String entity, String url, String name, Object value, Map<String, Object> context);

	void notifyListenersThereIsNoData(String entity, Map<String, Object> context);

	void addStatusListener(IRepositoryStatusListener listener);

	void removeStatusListener(IRepositoryStatusListener listener);

	void addLogger(ISoftwareFmLogger logger);

	void shutdown();

	static class Utils {

		public static ISoftwareFmRepository repository() {
			return repository(IRepositoryFacard.Utils.defaultFacard(), IJarDigester.Utils.digester());
		}

		public static ISoftwareFmRepository repository(IRepositoryFacard facard, IJarDigester jarDigester) {
			return new SoftwareFmRepository(facard, jarDigester);
		}

		public static Map<String, Object> makeSecondaryContext(String entity, String key, String rawUrl) {
			return Maps.<String, Object> makeMap(//
					RepositoryConstants.entity, entity, //
					RepositoryConstants.urlKey, key,//
					RepositoryConstants.rawUrl, rawUrl);
		}

		public static Map<String, Object> makePrimaryContext(String entity) {
			return Maps.<String, Object> makeMap(//
					RepositoryConstants.entity, entity); //
		}

		public static Map<String, Object> makeSecondaryNotFoundContext(String entity) {
			return Maps.<String, Object> makeMap(//
					RepositoryConstants.entity, entity); //
		}
	}

}
