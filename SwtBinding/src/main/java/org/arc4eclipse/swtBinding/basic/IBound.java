package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.httpClient.response.IResponse;

public interface IBound<Data extends IRepositoryDataItem> {

	void processResponse(IResponse response, Data data);
}
