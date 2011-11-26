/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.collections.explorer;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.card.RightClickCategoryResult.Type;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreMock;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.MemoryAfterEditCallback;
import org.softwareFm.display.swt.SwtIntegrationAndServiceTest;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.resources.ResourceGetterMock;

public class ExplorerTest extends SwtIntegrationAndServiceTest {

	private CardDataStoreMock updateStore;
	private MemoryAfterEditCallback afterEdit;
	private CardConfig raw;
	private int count;
	private MasterDetailSocial masterDetailSocial;

	public void testCreateNewItemMakesNewUrlBasedOnCardNameUrlKey() {
		//THe cardNameField should have no effect
		//THe keys are different to ensure different urls for the update store 
		// 0 is Strings.forUrl(name) 1 is guid
		checkCreateNewItemMakesNewUrl("rootUrl/key1/xnewItemNamex", "x{0}x", RightClickCategoryResult.Type.IS_COLLECTION, "noCardNameField", "newItemName", "key1");
		checkCreateNewItemMakesNewUrl("rootUrl/key2/xnewItemNamex", "x{0}x", RightClickCategoryResult.Type.IS_COLLECTION, "noCardNameField", "new Item Name", "key2");
		checkCreateNewItemMakesNewUrl("rootUrl/key3/randomUUID", "{1}", RightClickCategoryResult.Type.IS_COLLECTION, "noCardNameField", "newItemName", "key3");
		checkCreateNewItemMakesNewUrl("rootUrl/key4/randomUUID", "{1}", RightClickCategoryResult.Type.IS_COLLECTION, "withCardNameField", "newItemName", "key4");

		checkCreateNewItemMakesNewUrl("rootUrl/xnewItemName1", "x{0}1", RightClickCategoryResult.Type.ROOT_COLLECTION, "noCardNameField", "newItemName", "key5");
		checkCreateNewItemMakesNewUrl("rootUrl/xnewItemName2", "x{0}2", RightClickCategoryResult.Type.ROOT_COLLECTION, "noCardNameField", "new Item Name", "key6");
		checkCreateNewItemMakesNewUrl("rootUrl/randomUUID_1", "{1}_1", RightClickCategoryResult.Type.ROOT_COLLECTION, "noCardNameField", "newItemName", "key7");
		checkCreateNewItemMakesNewUrl("rootUrl/randomUUID_2", "{1}_2", RightClickCategoryResult.Type.ROOT_COLLECTION, "withCardNameField", "newItemName", "key8");
	}

	
	public void testCreatedItemHasCollectionTypeAndNameGivenByCardNameField(){
		checkCreatedItemInitialData("noCardNameField", "key1", "newItemName", null);
		checkCreatedItemInitialData("withCardNameField", "key2", "newItemName", "cardName");
		checkCreatedItemInitialData("withCardNameField", "key3", "new Item Name", "cardName");
	}


	private void checkCreatedItemInitialData(String collection, String key, String expectedName, String cardNameField) {
		Type type = RightClickCategoryResult.Type.IS_COLLECTION;
		Map<String,Object> raw = Maps.stringObjectMap(CardConstants.slingResourceType, collection);
		Map<String,Object>expected = cardNameField == null?raw: Maps.with(raw, cardNameField, expectedName);
			
		
		Map<String, Object> created = createNewItem( "{0}", type, collection, expectedName, key);
		assertEquals(expected, created);
	}
	
	private void checkCreateNewItemMakesNewUrl(String expectedUrl, String cardNameUrl, Type type, String collectionName, String newItemName, String key) {
		createNewItem(cardNameUrl, type, collectionName, newItemName, key);

		assertEquals(++count, updateStore.updateMap.size());
		assertTrue(updateStore.updateMap.get(key)+"\n\n" + updateStore.updateMap.toString(), updateStore.updateMap.containsKey(expectedUrl));
	}


	private Map<String, Object> createNewItem(String cardNameUrl, Type type, String collectionName, String newItemName, String key) {
		Explorer explorer = makeExplorer(cardNameUrl);

		RightClickCategoryResult result = new RightClickCategoryResult(type, collectionName, key, "rootUrl");
		return explorer.createNewItem(result, updateStore, newItemName, afterEdit);
	}

	private Explorer makeExplorer(String cardNameUrl) {
		CardConfig cardConfig = raw.withResourceGetterFn(IResourceGetter.Utils.mock(//
				Functions.call(raw.resourceGetterFn, null).with(new ResourceGetterMock(CardConstants.cardNameUrlKey, cardNameUrl)), // default
				"noCardNameField", new ResourceGetterMock(), //
				"withCardNameField", new ResourceGetterMock(CardConstants.cardNameFieldKey, "cardName")));
		Explorer explorer = new Explorer(cardConfig, "rootUrl", masterDetailSocial, service, IPlayListGetter.Utils.noPlayListGetter()) {
			@Override
			protected String makeRandomUUID() {
				return "randomUUID";
			}
		};
		return explorer;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		afterEdit = IAfterEditCallback.Utils.memory();
		updateStore = new CardDataStoreMock();
		masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
		raw = new CardConfig(ICardFactory.Utils.cardFactory(), updateStore);
	}
}