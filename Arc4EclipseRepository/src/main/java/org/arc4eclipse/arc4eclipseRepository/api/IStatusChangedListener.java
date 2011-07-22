package org.arc4eclipse.arc4eclipseRepository.api;

import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;

public interface IStatusChangedListener<T extends IRepositoryDataItem> {

	void statusChanged(String url, Class<? extends T> clazz, RepositoryDataItemStatus status, T item) throws Exception;

	public static class Utils {
		public static <T extends IRepositoryDataItem> IStatusChangedListener<T> sysout() {
			return new IStatusChangedListener<T>() {
				@Override
				public void statusChanged(String url, Class<? extends T> clazz, RepositoryDataItemStatus status, T item) {
					System.out.println(status + " " + clazz.getSimpleName() + " " + item);
				}
			};
		}

		public static <T extends IRepositoryDataItem> MemoryStatusChangedListener<T> memory() {
			return new MemoryStatusChangedListener<T>();
		};

		public static IStatusChangedListener<IJarData> requestMoreWhenGotJarData(final IArc4EclipseRepository repository) {
			return new IStatusChangedListener<IJarData>() {
				@Override
				public void statusChanged(String url, Class<? extends IJarData> clazz, RepositoryDataItemStatus status, IJarData item) throws Exception {
					if (item != null && status == RepositoryDataItemStatus.FOUND) {
						IUrlGenerator generator = repository.generator();
						repository.getData(generator.forOrganisation().apply(item.getOrganisationUrl()), IOrganisationData.class);
						repository.getData(generator.forProject().apply(item.getProjectUrl()), IProjectData.class);
					}

				}
			};

		}
	}
}
