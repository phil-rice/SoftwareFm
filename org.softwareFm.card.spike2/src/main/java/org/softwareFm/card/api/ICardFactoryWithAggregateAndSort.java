package org.softwareFm.card.api;

import java.util.List;
import java.util.Map;

public interface ICardFactoryWithAggregateAndSort extends ICardFactory{
	List<KeyValue> aggregateAndSort(Map<String, Object> raw);
}
