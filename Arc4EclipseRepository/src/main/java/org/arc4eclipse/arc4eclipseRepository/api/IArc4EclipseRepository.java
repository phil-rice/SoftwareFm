package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.concurrent.Future;

import org.arc4eclipse.arc4eclipseRepository.api.impl.Arc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.impl.UrlGenerator;
import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;

public interface IArc4EclipseRepository {

	Future<?> getJarData(String jarDigest);

	Future<?> modifyJarData(String jarDigest, String name, Object value);

	Future<?> getData(String url);

	Future<?> modifyData(String url, String name, Object value);

	IUrlGenerator generator();

	void addStatusListener(IStatusChangedListener listener);

	void addLogger(IArc4EclipseLogger logger);

	void shutdown();

	static class Utils {

		public static IArc4EclipseRepository repository() {
			return repository(IRepositoryFacard.Utils.defaultFacard(), new UrlGenerator(), IJarDigester.Utils.digester());
		}

		public static IArc4EclipseRepository repository(IRepositoryFacard facard, IUrlGenerator urlGenerator, IJarDigester jarDigester) {
			return new Arc4EclipseRepository(facard, urlGenerator, jarDigester);
		}

	}

}
