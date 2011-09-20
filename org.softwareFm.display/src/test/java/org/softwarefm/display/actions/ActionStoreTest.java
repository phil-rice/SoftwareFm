package org.softwareFm.display.actions;

import org.softwareFm.display.IAction;
import org.softwareFm.utilities.maps.AbstractSimpleMapTest;
import org.softwareFm.utilities.maps.ISimpleMap;

public class ActionStoreTest extends AbstractSimpleMapTest<String, IAction> {

	public void testThrowsExceptionIfKeyNotThere() {
		IllegalArgumentException e = checkThrowsExceptionIfKeyNotThere();
		assertEquals("Map doesn't have key b. Legal keys are [a]. Map is {a=ActionMock [name=1]}", e.getMessage());
	}

	@Override
	protected String makeKey(String seed) {
		return seed;
	}

	@Override
	protected IAction makeValue(String seed) {
		return new ActionMock(seed);
	}

	@Override
	protected ISimpleMap<String, IAction> blankMap() {
		return new ActionStore();
	}

	@Override
	protected void put(ISimpleMap<String, IAction> map, String key, IAction value) {
		assertSame(map, ((ActionStore) map).action(key, value));
	}

	@Override
	protected String duplicateKeyErrorMessage() {
		return "Cannot set value of action twice. Current value [ActionMock [name=1]]. New value [ActionMock [name=2]]";
	}

}
