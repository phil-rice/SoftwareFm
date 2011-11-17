package org.softwareFm.card.api;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;

public class AddItemProcessorMock implements IAddItemProcessor{

	public List<RightClickCategoryResult> results = Lists.newList();

	@Override
	public void process(RightClickCategoryResult rightClickCategoryResult) {
		results.add(rightClickCategoryResult);
	}

}
