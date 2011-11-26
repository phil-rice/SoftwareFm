/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpClient.response.impl;

import org.softwareFm.httpClient.response.IResponse;

public class Response implements IResponse {

	private final int statusCode;
	private final String string;
	private final String url;

	public Response(String url, int statusCode, String string) {
		this.url = url;
		this.statusCode = statusCode;
		this.string = string;
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
		return "Response [url=" + url + ", statusCode=" + statusCode + ", string=" + string + "]";
	}

}