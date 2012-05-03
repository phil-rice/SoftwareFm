package org.softwareFm.crowdsource.utilities.collections;

import java.util.Set;

import junit.framework.TestCase;

public class SetsTest extends TestCase {

	public void testAsMutableSimpleSet() {
		Set<Integer> wrapped = Sets.newSet();
		IMutableSimpleSet<Integer> set = Sets.asMutableSimpleSet(wrapped);

		assertFalse(set.contains(1));
		assertFalse(set.contains(2));
		assertFalse(set.contains(3));

		set.add(1);
		wrapped.add(2);
		assertTrue(set.contains(1));
		assertTrue(set.contains(2));
		assertFalse(set.contains(3));
	}

}
