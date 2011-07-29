package org.arc4eclipse.repositoryFacard.impl;

import java.util.Map;

import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.utilities.maps.Maps;

public class RepositoryWithSingleValueTests extends AbstractRepositoryFacardTest {
	private final String url = "/tests/" + RepositoryWithSingleValueTests.class.getSimpleName();

	public void testPuttingSingleValuesIntoExistingObject() throws Exception {
		facard.delete(url, IResponseCallback.Utils.memoryCallback()).get();
		Map<String, Object> initial = Maps.<String, Object> makeMap("a", 1L, "b", "2", "c", "3");

		ResponseCallbackRecordingStatus postCallback = new ResponseCallbackRecordingStatus();
		facard.post(url, initial, postCallback).get();
		checkDataMatches(url, initial);

		facard.post(url, Maps.<String, Object> makeMap("a", "changed"), postCallback).get();
		checkDataMatches(url, "a", "changed", "b", "2", "c", "3");
	}

	@Override
	protected void setUp() throws Exception {
		facard = IRepositoryFacard.Utils.defaultFacard();
	};

}
