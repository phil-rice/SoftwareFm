package org.softwareFm.card.api;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;

public class AddItemProcessorMock implements IAddItemProcessor{

	public List<RightClickCategoryResult> results = Lists.newList();
	public int newArtifactCount;

	@Override
	public void addCollectionItem(RightClickCategoryResult rightClickCategoryResult) {
		results.add(rightClickCategoryResult);
	}

	@Override
	public void addNewArtifact() {
		newArtifactCount++;
	}

}
