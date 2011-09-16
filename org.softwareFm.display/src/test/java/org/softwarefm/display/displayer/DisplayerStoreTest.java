package org.softwarefm.display.displayer;

import org.softwareFm.utilities.maps.AbstractSimpleMapTest;
import org.softwareFm.utilities.maps.ISimpleMap;

public class DisplayerStoreTest extends AbstractSimpleMapTest<String, IDisplayerFactory> {

	@Override
	protected String makeKey(String seed) {
		return seed;
	}

	@Override
	protected IDisplayerFactory makeValue(String seed) {
		return new DisplayerMock(seed);
	}

	@Override
	protected ISimpleMap<String, IDisplayerFactory> blankMap() {
		return new DisplayerStore();
	}

	@Override
	protected void put(ISimpleMap<String, IDisplayerFactory> map, String key, IDisplayerFactory value) {
		assertSame(map, ((DisplayerStore) map).displayer(key, value));
	}

	public void testThrowsExceptionIfKeyNotThere() {
		IllegalArgumentException e = checkThrowsExceptionIfKeyNotThere();
		assertEquals("Map doesn't have key b. Legal keys are [a]. Map is {a=DisplayerMock [seed=1]}", e.getMessage());
	}

	@Override
	protected String duplicateKeyErrorMessage() {
		return "Cannot set value of displayer twice. Current value [DisplayerMock [seed=1]]. New value [DisplayerMock [seed=2]]";
	}

}
