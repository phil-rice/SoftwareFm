package org.softwarefm.display.lists;

import org.softwareFm.utilities.maps.AbstractSimpleMapTest;
import org.softwareFm.utilities.maps.ISimpleMap;

public class ListEditorStoreTest extends AbstractSimpleMapTest<String, IListEditor> {

	public void testThrowsExceptionIfKeyNotThere() {
		checkThrowsExceptionIfKeyNotThere();
	}

	@Override
	protected String makeKey(String seed) {
		return seed;
	}

	@Override
	protected IListEditor makeValue(String seed) {
		return new ListEditorMock(seed);
	}

	@Override
	protected ISimpleMap<String, IListEditor> blankMap() {
		return new ListEditorStore();
	}

	@Override
	protected String duplicateKeyErrorMessage() {
		return "Cannot set value of a twice. Current value [ListEditorMock [seed=1]]. New value [ListEditorMock [seed=2]]";
	}

	@Override
	protected void put(ISimpleMap<String, IListEditor> map, String key, IListEditor value) {
		assertSame(map, ((ListEditorStore) map).register(key, value));

	}

}
