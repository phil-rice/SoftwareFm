/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.card.internal.details;

import org.softwareFm.swt.details.internal.CollectionsDetailAdder;

public class CollectionsDetailAdderTest extends AbstractDetailsAdderTest<CollectionsDetailAdder> {

	public void testWithNonListGetNull() {
		checkGetNull(detailFactory, stringValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testNeedMoreThanItBeingAList() {
		checkGetNull(detailFactory, mapValue);
		checkGetNull(detailFactory, folderValue);
		checkGetNull(detailFactory, typedValueNotCollection);
	}

	public void testNeedsCollectionToMakeACardHolder() {
		checkGetCardCollectionsHolder(collectionValue, parentCard.url());
	}

	@Override
	protected CollectionsDetailAdder makeDetailsAdder() {
		return new CollectionsDetailAdder();
	}

}