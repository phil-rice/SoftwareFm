package org.arc4eclipse.RepositoryCache.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.arc4eclipse.RepositoryCache.IRepositoryFrontEnd;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.utilities.callbacks.MemoryCallback;
import org.arc4eclipse.utilities.functions.Functions;
import org.arc4eclipse.utilities.maps.Maps;

public class RepositoryFrontEndTest extends TestCase {

	private final class ResponseCallbackRecordingStatus implements IResponseCallback {
		public AtomicInteger statusCode = new AtomicInteger();
		public AtomicReference<String> responseString = new AtomicReference<String>();

		@Override
		public void process(IResponse response) {
			statusCode.set(response.statusCode());
			responseString.set(response.asString());
		}

		public void assertOk() {
			int code = statusCode.get();
			assertTrue("Code: " + code + "\n" + responseString.get(), code == 200 || code == 201);
		}
	}

	private IRepositoryFrontEnd repositoryFrontEnd;

	public void testWithSimpleTypes() {
		checkRoundTrip();
		checkRoundTrip("a", 1L);
		checkRoundTrip("a", false);
		checkRoundTrip("a", true);
		checkRoundTrip("a", "1");
		checkRoundTrip("a", "1", "b", 2L);
		checkRoundTripWith(Maps.makeMap(new Object[] { "a", 1L }), Maps.makeMap(new Object[] { "a", 1 }));
	}

	public void testWithArrays() {
		checkRoundTripWithArrays("a", new Long[] { 1L, 2L });
		checkRoundTripWithArrays("a", new Boolean[] { false, true, false });
		checkRoundTripWithArrays("a", new String[] { "1", "2" });
		checkRoundTripWithArrays(Maps.makeMap(new Object[] { "a", new Long[] { 1L, 2L } }), Maps.makeMap(new Object[] { "a", new Integer[] { 1, 2 } }));
	}

	public void testGetWithDepth() {
		String url = "/" + getClass().getSimpleName();
		repositoryFrontEnd.delete(url, new ResponseCallbackRecordingStatus());
		ResponseCallbackRecordingStatus oneReponse = new ResponseCallbackRecordingStatus();
		ResponseCallbackRecordingStatus twoReponse = new ResponseCallbackRecordingStatus();
		ResponseCallbackRecordingStatus parentReponse = new ResponseCallbackRecordingStatus();
		repositoryFrontEnd.post(url, Maps.<String, Object> makeMap("namep1", "datap1", "namep2", 2L), parentReponse);
		repositoryFrontEnd.post(url + "/one", Maps.<String, Object> makeMap("name11", "data11", "name12", 12L), oneReponse);
		repositoryFrontEnd.post(url + "/two", Maps.<String, Object> makeMap("name21", "data21", "name22", 22L), twoReponse);
		parentReponse.assertOk();
		oneReponse.assertOk();
		twoReponse.assertOk();

		checkDepthMatches(url, 0, "namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured");
		checkDepthMatches(url, 1, "namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured", //
				"one", Maps.makeMap("name11", "data11", "name12", 12L, "jcr:primaryType", "nt:unstructured"),//
				"two", Maps.makeMap("name21", "data21", "name22", 22L, "jcr:primaryType", "nt:unstructured"));
	}

	public void testPostImport() {
		String url = "/" + getClass().getSimpleName();
		repositoryFrontEnd.delete(url, new ResponseCallbackRecordingStatus());

		Map<String, Object> expected = Maps.makeMap("namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured", //
				"one", Maps.makeMap("name11", "data11", "name12", 12L, "jcr:primaryType", "nt:unstructured"),//
				"two", Maps.makeMap("name21", "data21", "name22", 22L, "jcr:primaryType", "nt:unstructured"));
		ResponseCallbackRecordingStatus parentReponse = new ResponseCallbackRecordingStatus();
		repositoryFrontEnd.postMany(url, expected, parentReponse);
		parentReponse.assertOk();

		checkDepthMatches(url, 0, "namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured");
		checkDepthMatches(url, 1, expected);
	}

	private void checkDepthMatches(String url, int depth, Object... expectedAsParams) {
		Map<String, Object> expected = Maps.<String, Object> makeMap(expectedAsParams);
		checkDepthMatches(url, depth, expected);
	}

	private void checkDepthMatches(String url, int depth, Map<String, Object> expected) {
		MemoryCallback<Map<String, Object>> callback = new MemoryCallback<Map<String, Object>>();
		repositoryFrontEnd.getDepth(url, depth, callback);
		List<Map<String, Object>> result = callback.getResult();
		assertEquals(1, result.size());
		Maps.assertEquals(expected, result.get(0));
	}

	public void testWierdStuff() {
		checkRoundTrip("a", "");
	}

	private void checkRoundTrip(Object... objects) {
		Map<String, Object> data = Maps.makeMap(objects);
		checkRoundTripWith(data, data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkRoundTripWith(Map expected, Map data) {
		String url = "/" + getClass().getSimpleName();

		ResponseCallbackRecordingStatus deleteCallback = new ResponseCallbackRecordingStatus();
		repositoryFrontEnd.delete(url, deleteCallback);
		// deleteCallback.assertOk();

		ResponseCallbackRecordingStatus postCallback = new ResponseCallbackRecordingStatus();
		repositoryFrontEnd.post(url, data, postCallback);
		postCallback.assertOk();

		MemoryCallback<Map<String, Object>> callback = new MemoryCallback<Map<String, Object>>();
		repositoryFrontEnd.get(url, callback);
		List<Map<String, Object>> calledData = callback.getResult();
		assertEquals(1, calledData.size());
		expected.put("jcr:primaryType", "nt:unstructured");
		Map<String, Object> actual = calledData.get(0);
		Maps.assertEquals(expected, actual);
	}

	private void checkRoundTripWithArrays(Object... objects) {
		Map<String, Object> data = Maps.makeMap(objects);
		checkRoundTripWithArrays(data, data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkRoundTripWithArrays(Map expected, Map data) {
		String url = "/" + getClass().getSimpleName();

		ResponseCallbackRecordingStatus deleteCallback = new ResponseCallbackRecordingStatus();
		repositoryFrontEnd.delete(url, deleteCallback);
		// deleteCallback.assertOk();

		ResponseCallbackRecordingStatus postCallback = new ResponseCallbackRecordingStatus();
		repositoryFrontEnd.post(url, data, postCallback);
		postCallback.assertOk();

		MemoryCallback<Map<String, Object>> callback = new MemoryCallback<Map<String, Object>>();
		repositoryFrontEnd.get(url, callback);
		List<Map<String, Object>> calledData = callback.getResult();
		assertEquals(1, calledData.size());
		expected.put("jcr:primaryType", "nt:unstructured");
		Map<String, Object> actual = Maps.mapTheMap(calledData.get(0), Functions.arraysBecomeLists());
		expected = Maps.mapTheMap(expected, Functions.arraysBecomeLists());
		Maps.assertEquals(expected, actual);
	}

	@Override
	protected void setUp() throws Exception {
		repositoryFrontEnd = IRepositoryFrontEnd.Utils.defaultFrontEnd();
	};

}
