/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repositoryFacard.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;

public class ResponseFacardCallbackRecordingStatus implements IRepositoryFacardCallback {
	public AtomicInteger statusCode = new AtomicInteger();
	public AtomicReference<IResponse> response = new AtomicReference<IResponse>();
	public AtomicReference<Map<String, Object>> data = new AtomicReference<Map<String, Object>>();
	public AtomicInteger count = new AtomicInteger();

	@Override
	public void process(IResponse response, Map<String, Object> data) {
		statusCode.set(response.statusCode());
		this.response.set(response);
		this.data.set(data);
		if (count.incrementAndGet() > 1)
			RepositoryFacardTest.fail();

	}

	public void assertOk() {
		int code = statusCode.get();
		RepositoryFacardTest.assertTrue("Code: " + code + "\n" + response.get(), code == 200 || code == 201);
	}

}