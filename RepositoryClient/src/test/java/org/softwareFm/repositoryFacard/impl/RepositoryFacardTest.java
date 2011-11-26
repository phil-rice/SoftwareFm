/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repositoryFacard.impl;

import java.util.Map;

import org.softwareFm.repositoryFacard.AbstractRepositoryFacardTest;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;

public class RepositoryFacardTest extends AbstractRepositoryFacardTest {

	public void testWithSimpleTypes() throws Exception {
		checkRoundTrip();
		checkRoundTrip("a", 1L);
		checkRoundTrip("a", false);
		checkRoundTrip("a", true);
		checkRoundTrip("a", "1");
		checkRoundTrip("a", "1", "b", 2L);
		checkRoundTripWith(Maps.makeMap(new Object[] { "a", 1L }), Maps.makeMap(new Object[] { "a", 1 }));
	}

	public void testWithArrays() throws Exception {
		checkRoundTripWithArrays("a", new Long[] { 1L, 2L });
		checkRoundTripWithArrays("a", new Boolean[] { false, true, false });
		checkRoundTripWithArrays("a", new String[] { "1", "2" });
		checkRoundTripWithArrays(Maps.makeMap(new Object[] { "a", new Long[] { 1L, 2L } }), Maps.makeMap(new Object[] { "a", new Integer[] { 1, 2 } }));
	}

	public void testGetWithDepth() throws Exception {
		String url = "/tests/" + getClass().getSimpleName();
		facard.delete(url, new ResponseCallbackRecordingStatus()).get();
		ResponseCallbackRecordingStatus oneReponse = new ResponseCallbackRecordingStatus();
		ResponseCallbackRecordingStatus twoReponse = new ResponseCallbackRecordingStatus();
		ResponseCallbackRecordingStatus parentReponse = new ResponseCallbackRecordingStatus();
		facard.post(url, Maps.<String, Object> makeMap("namep1", "datap1", "namep2", 2L), parentReponse).get();
		facard.post(url + "/one", Maps.<String, Object> makeMap("name11", "data11", "name12", 12L), oneReponse).get();
		facard.post(url + "/two", Maps.<String, Object> makeMap("name21", "data21", "name22", 22L), twoReponse).get();
		parentReponse.assertOk();
		oneReponse.assertOk();
		twoReponse.assertOk();

		checkDepthMatches(url, 0, "namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured");
		checkDepthMatches(url, 1, "namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured", //
				"one", Maps.makeMap("name11", "data11", "name12", 12L, "jcr:primaryType", "nt:unstructured"),//
				"two", Maps.makeMap("name21", "data21", "name22", 22L, "jcr:primaryType", "nt:unstructured"));
	}

	public void testPostImport() throws Exception {
		String url = "/tests/" + getClass().getSimpleName();
		facard.delete(url, new ResponseCallbackRecordingStatus()).get();

		Map<String, Object> expected = Maps.makeMap("namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured", //
				"one", Maps.makeMap("name11", "data11", "name12", 12L, "jcr:primaryType", "nt:unstructured"),//
				"two", Maps.makeMap("name21", "data21", "name22", 22L, "jcr:primaryType", "nt:unstructured"));
		ResponseCallbackRecordingStatus parentReponse = new ResponseCallbackRecordingStatus();
		facard.postMany(url, expected, parentReponse).get();
		parentReponse.assertOk();

		checkDepthMatches(url, 0, "namep1", "datap1", "namep2", 2L, "jcr:primaryType", "nt:unstructured");
		checkDepthMatches(url, 1, expected);
	}

	private void checkDepthMatches(String url, int depth, Object... expectedAsParams) throws Exception {
		Map<String, Object> expected = Maps.<String, Object> makeMap(expectedAsParams);
		checkDepthMatches(url, depth, expected);
	}

	private void checkDepthMatches(String url, int depth, Map<String, Object> expected) throws Exception {
		ResponseFacardCallbackRecordingStatus callback = new ResponseFacardCallbackRecordingStatus();
		facard.getDepth(url, depth, callback).get();
		Maps.assertEquals(expected, callback.data.get());
	}

	public void _testWierdStuff() throws Exception {
		checkRoundTrip("a", "");
	}

	private void checkRoundTrip(Object... objects) throws Exception {
		Map<String, Object> data = Maps.makeMap(objects);
		checkRoundTripWith(data, data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkRoundTripWith(Map expected, Map data) throws Exception {
		String url = "/tests/" + getClass().getSimpleName();

		ResponseCallbackRecordingStatus deleteCallback = new ResponseCallbackRecordingStatus();
		facard.delete(url, deleteCallback).get();
		// deleteCallback.assertOk();

		ResponseCallbackRecordingStatus postCallback = new ResponseCallbackRecordingStatus();
		facard.post(url, data, postCallback).get();
		postCallback.assertOk();

		checkDataMatches(url, expected);
	}

	private void checkRoundTripWithArrays(Object... objects) throws Exception {
		Map<String, Object> data = Maps.makeMap(objects);
		checkRoundTripWithArrays(data, data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkRoundTripWithArrays(Map expected, Map data) throws Exception {
		String url = "/tests/" + getClass().getSimpleName();

		ResponseCallbackRecordingStatus deleteCallback = new ResponseCallbackRecordingStatus();
		facard.delete(url, deleteCallback).get();
		// deleteCallback.assertOk();

		ResponseCallbackRecordingStatus postCallback = new ResponseCallbackRecordingStatus();
		facard.post(url, data, postCallback).get();
		postCallback.assertOk();

		ResponseFacardCallbackRecordingStatus callback = new ResponseFacardCallbackRecordingStatus();
		facard.get(url, callback).get();
		Map<String, Object> actual = Maps.mapTheMap(callback.data.get(), Functions.arraysBecomeLists());
		expected = Maps.mapTheMap(expected, Functions.arraysBecomeLists());
		expected.put("jcr:primaryType", "nt:unstructured");
		Maps.assertEquals(expected, actual);
	}

}