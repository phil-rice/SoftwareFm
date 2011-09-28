package org.softwareFm.display.smallButtons;

import org.junit.Test;
import org.softwareFm.utilities.maps.AbstractSimpleMapTest;
import org.softwareFm.utilities.maps.ISimpleMap;

public class SmallButtonStoreTest extends AbstractSimpleMapTest<String, ISmallButtonFactory> {

	@Test
	public void testThrowsExceptionIfKeyNotThere() {
		checkThrowsExceptionIfKeyNotThere("Map doesn't have key b. Legal keys are [a]. Map is {a=SmallButtonFactoryMock [seed=1]}");
	}

	@Override
	protected String makeKey(String seed) {
		return seed;
	}

	@Override
	protected ISmallButtonFactory makeValue(String seed) {
		return new SmallButtonFactoryMock(seed);
	}

	@Override
	protected ISimpleMap<String, ISmallButtonFactory> blankMap() {
		return new SmallButtonStore();
	}

	@Override
	protected void put(ISimpleMap<String, ISmallButtonFactory> map, String key, ISmallButtonFactory value) {
		SmallButtonStore store = (SmallButtonStore) map;
		assertSame(store, store.smallButton(key, value));

	}

	@Override
	protected String duplicateKeyErrorMessage() {
		return "Cannot set value of smallButton twice. Current value [SmallButtonFactoryMock [seed=1]]. New value [SmallButtonFactoryMock [seed=2]]";
	}
}
