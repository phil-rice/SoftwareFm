package org.softwareFm.repositoryFacard.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;

public abstract class AbstractRepositoryFacardTest extends TestCase {
	protected IRepositoryFacard facard;

	@Override
	protected void setUp() throws Exception {
		facard = IRepositoryFacard.Utils.defaultFacard();
	}

	protected void checkDataMatches(String url, Object... expectedObjects) throws Exception {
		checkDataMatches(url, Maps.makeImmutableMap(expectedObjects));
	}

	protected void checkDataMatches(String url, @SuppressWarnings("rawtypes") Map rawExpected) throws Exception {
		ResponseFacardCallbackRecordingStatus callback = new ResponseFacardCallbackRecordingStatus();
		facard.get(url, callback).get();
		checkCallbackHasMapRepresenting(callback, rawExpected);
	}

	@SuppressWarnings("unchecked")
	protected void checkCallbackHasMapRepresenting(ResponseFacardCallbackRecordingStatus callback, @SuppressWarnings("rawtypes") Map rawExpected) {
		callback.assertOk();
		HashMap<String, Object> expected = new HashMap<String, Object>(rawExpected);
		expected.put("jcr:primaryType", "nt:unstructured");
		Maps.assertEquals(expected, callback.data.get());
	};
}
