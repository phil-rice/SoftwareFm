package org.arc4eclipse.arc4eclipseRepository.data;

import org.arc4eclipse.arc4eclipseRepository.data.impl.JarData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.OrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ProjectData;
import org.arc4eclipse.utilities.maps.ISimpleMap;
import org.arc4eclipse.utilities.maps.Maps;

public interface IRepositoryDataItem extends ISimpleMap<String, Object> {
	String getString(String key);

	public class Utils {
		public static IJarData jarData(String... namesAndValues) {
			return new JarData(Maps.<String, Object> makeMap((Object[]) namesAndValues));
		}

		public static IOrganisationData organisationData(String... namesAndValues) {
			return new OrganisationData(Maps.<String, Object> makeMap((Object[]) namesAndValues));
		}

		public static IProjectData projectData(String... namesAndValues) {
			return new ProjectData(Maps.<String, Object> makeMap((Object[]) namesAndValues));
		}
	}
}
