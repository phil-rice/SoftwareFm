/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpClient.response.internal;

import org.softwareFm.httpClient.response.IResponse;

public class Response implements IResponse {

	private final int statusCode;
	private final String string;
	private final String url;
	private final String mimeType;

	public Response(int statusCode, String url, String string, String mimeType) {
		this.statusCode = statusCode;
		this.string = string;
		this.url = url;
		this.mimeType = mimeType;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public int statusCode() {
		return statusCode;
	}

	@Override
	public String asString() {
		return string;
	}

	@Override
	public String toString() {
		return "Response [statusCode=" + statusCode + ", string=" + string + ", url=" + url + ", mimeType=" + mimeType + "]";
	}

	@Override
	public String mimeType() {
		return mimeType;
	}
}