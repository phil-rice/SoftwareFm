/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.http.internal;

import java.util.List;

import org.apache.http.Header;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.http.IResponse;

public class Response implements IResponse {

	private final int statusCode;
	private final String string;
	private final String url;
	private final String mimeType;
	private final List<Header> headers;

	public Response(int statusCode, String url, String string, String mimeType, List<Header> headers) {
		this.statusCode = statusCode;
		this.string = string;
		this.url = url;
		this.mimeType = mimeType;
		this.headers = Lists.immutableCopy(headers);
	}

	public List<Header> headers() {
		return headers;
	}
	public String url() {
		return url;
	}

	
	public int statusCode() {
		return statusCode;
	}

	
	public String asString() {
		return string;
	}

	
	@Override
	public String toString() {
		return "Response [statusCode=" + statusCode + ", string=" + string + ", url=" + url + ", mimeType=" + mimeType + ", headers=" + headers + "]";
	}

	
	public String mimeType() {
		return mimeType;
	}
}