package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;

public interface IBoundRepository {

	<T extends IRepositoryDataItem> void getData(String url, Class<T> clazz);

	<T extends IRepositoryDataItem> void changeData(String url, String key, Object newValue, Class<T> clazz);
}
