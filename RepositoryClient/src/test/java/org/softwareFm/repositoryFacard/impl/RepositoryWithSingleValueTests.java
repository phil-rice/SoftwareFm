/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repositoryFacard.impl;

import java.util.Map;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.AbstractRepositoryFacardTest;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;

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