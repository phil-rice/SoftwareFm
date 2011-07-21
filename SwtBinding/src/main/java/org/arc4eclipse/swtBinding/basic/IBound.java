package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;

public interface IBound<Data extends IRepositoryDataItem> {

	void processResponse(RepositoryDataItemStatus status, Data data);

	void clear();
}
