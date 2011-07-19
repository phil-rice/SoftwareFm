package org.arc4eclipse.arc4eclipseRepository.data;

import org.arc4eclipse.utilities.maps.ISimpleMap;

public interface IRepositoryDataItem extends ISimpleMap<String, Object> {
	String getString(String key);
}
