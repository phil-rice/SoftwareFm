/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repositoryFacard;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.repositoryFacard.impl.ResponseFacardCallbackRecordingStatus;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.tests.INeedsServerTest;

public abstract class AbstractRepositoryFacardTest extends TestCase implements INeedsServerTest {
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