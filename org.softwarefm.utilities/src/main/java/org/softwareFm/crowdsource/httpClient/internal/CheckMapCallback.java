/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.httpClient.internal;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class CheckMapCallback implements IResponseCallback {

	private final int status;
	private final AtomicInteger count = new AtomicInteger();
	private final AtomicInteger succeeded = new AtomicInteger();
	private final Map<String, Object> expected;

	public CheckMapCallback(int status, Object... namesAndValues) {
		this.status = status;
		this.expected = Maps.stringObjectLinkedMap(namesAndValues);
	}

	@Override
	public void process(IResponse response) {
		count.incrementAndGet();
		Assert.assertEquals(expected, Json.mapFromString(response.asString()));
		Assert.assertEquals(status, response.statusCode());
		succeeded.incrementAndGet();
	}

	public void assertCalledSuccessfullyOnce() {
		Assert.assertEquals(1, count.get());
		Assert.assertEquals(1, succeeded.get());
	}

}