package org.arc4eclipse.panel;

import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.arc4eclipse.swtBinding.basic.ICurrentUrlCalculator;
import org.arc4eclipse.utilities.functions.IFunction1;

public interface IRepositoryDataPanel<T extends IRepositoryDataItem> extends ICurrentUrlCalculator, IStatusChangedListener<T> {

	void setPrimaryKey(String primaryKey);

	List<IBound<T>> boundChildren();

	Class<T> getDataClass();

	IFunction1<Map<String, Object>, T> mapper();

}
