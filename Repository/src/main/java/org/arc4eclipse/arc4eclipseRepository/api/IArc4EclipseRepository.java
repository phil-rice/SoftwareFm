package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.arc4eclipse.arc4eclipseRepository.api.impl.Arc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.utilities.maps.Maps;

public interface IArc4EclipseRepository {

	Future<?> getData(String entity, String url, Map<String, Object> context);

	Future<?> modifyData(String entity, String url, String name, Object value, Map<String, Object> context);

	void addStatusListener(IRepositoryStatusListener listener);

	void removeStatusListener(IRepositoryStatusListener listener);

	void addLogger(IArc4EclipseLogger logger);

	void shutdown();

	static class Utils {

		public static IArc4EclipseRepository repository(IUrlGeneratorMap urlGeneratorMap) {
			return repository(IRepositoryFacard.Utils.defaultFacard(), urlGeneratorMap, IJarDigester.Utils.digester());
		}

		public static IArc4EclipseRepository repository(IRepositoryFacard facard, IUrlGeneratorMap urlGeneratorMap, IJarDigester jarDigester) {
			return new Arc4EclipseRepository(facard, jarDigester);
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
	}

}
