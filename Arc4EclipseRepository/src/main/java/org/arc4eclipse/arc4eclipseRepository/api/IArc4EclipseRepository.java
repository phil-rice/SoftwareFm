package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.arc4eclipse.arc4eclipseRepository.api.impl.Arc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.impl.UrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.arc4eclipseRepository.data.impl.JarData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.OrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ProjectData;
import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.utilities.functions.IFunction1;

public interface IArc4EclipseRepository {

	Future<?> getJarData(String jarDigest);

	Future<?> modifyJarData(String jarDigest, String name, Object value);

	<T extends IRepositoryDataItem> Future<?> getData(String url, Class<T> clazz);

	<T extends IRepositoryDataItem> Future<?> modifyData(String url, String name, Object value, Class<T> clazz);

	IUrlGenerator generator();

	<T extends IRepositoryDataItem> void addStatusListener(Class<T> clazz, IStatusChangedListener<T> listener);

	void addLogger(IArc4EclipseLogger logger);

	void shutdown();

	static class Utils {

		public static IArc4EclipseRepository repository() {
			return repository(IRepositoryFacard.Utils.defaultFacard(), new UrlGenerator(), IJarDigester.Utils.digester());
		}

		public static IArc4EclipseRepository repository(IRepositoryFacard facard, IUrlGenerator urlGenerator, IJarDigester jarDigester) {
			return new Arc4EclipseRepository(facard, urlGenerator, jarDigester);
		}

		public static IFunction1<Map<String, Object>, IJarData> jarData() {
			return new IFunction1<Map<String, Object>, IJarData>() {
				@Override
				public IJarData apply(Map<String, Object> from) throws Exception {
					return new JarData(from);
				}
			};
		}

		public static IFunction1<Map<String, Object>, IOrganisationData> organisationData() {
			return new IFunction1<Map<String, Object>, IOrganisationData>() {
				@Override
				public IOrganisationData apply(Map<String, Object> from) throws Exception {
					return new OrganisationData(from);
				}
			};
		}

		public static IFunction1<Map<String, Object>, IProjectData> projectData() {
			return new IFunction1<Map<String, Object>, IProjectData>() {
				@Override
				public IProjectData apply(Map<String, Object> from) throws Exception {
					return new ProjectData(from);
				}
			};
		}

	}

}
