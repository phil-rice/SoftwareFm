package org.arc4eclipse.repositoryFacard.impl;

import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.utilities.maps.Maps;

public class AspectToParametersTest extends TestCase {

	public void testAspectToParameters() {
		checkAspectToParameters(Maps.makeLinkedMap("a", 1, "b", 2), "a", "1", "b", "2");
	}

	private void checkAspectToParameters(Map<Object, Object> map, String... expected) {
		AspectToParameters<Object, Object> aspectToParameters = new AspectToParameters<Object, Object>();
		String[] actual = aspectToParameters.makeParameters("someThing", "someAspect", map);
		assertTrue("Expected: " + Arrays.asList(expected) + "\nActual:  " + Arrays.asList(actual), Arrays.equals(expected, actual));

	}

}
