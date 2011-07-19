package org.arc4eclipse.arc4eclipseRepository.api;

import java.io.File;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.impl.Arc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.impl.JarDigestor;
import org.arc4eclipse.arc4eclipseRepository.api.impl.UrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.IReleaseData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.JarData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.OrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ReleaseData;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.utilities.functions.IFunction1;

public interface IArc4EclipseRepository extends ICleansCache {

	void getJarData(File jar, IArc4EclipseCallback<IJarData> callback);

	<T> void getData(String url, IFunction1<Map<String, Object>, T> mapper, IArc4EclipseCallback<T> callback);

	void modifyJarData(File jar, String name, Object value, IArc4EclipseCallback<IJarData> callback);

	<T> void modifyData(String url, String name, Object value, IFunction1<Map<String, Object>, T> mapper, IArc4EclipseCallback<T> callback);

	IUrlGenerator generator();

	static class Utils {

		public static IArc4EclipseRepository repository() {
			return repository(IRepositoryFacard.Utils.defaultFacard(), new UrlGenerator(), new JarDigestor());
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

		public static IFunction1<Map<String, Object>, IReleaseData> releaseData() {
			return new IFunction1<Map<String, Object>, IReleaseData>() {
				@Override
				public IReleaseData apply(Map<String, Object> from) throws Exception {
					return new ReleaseData(from);
				}
			};
		}

	}
}
