/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpClient.requests;

import org.softwareFm.httpClient.response.IResponse;

public interface IResponseCallback {

	void process(IResponse response);

	public static class Utils {
		public static IResponseCallback noCallback() {
			return new IResponseCallback() {
				@Override
				public void process(IResponse response) {
				}
			};
		}

		public static <Thing, Aspect> MemoryResponseCallback<Thing, Aspect> memoryCallback() {
			return new MemoryResponseCallback<Thing, Aspect>();
		}

		public static IResponseCallback sysoutStatusCallback() {
			return new IResponseCallback() {
				@Override
				public void process(IResponse response) {
					System.out.println(response.url() + " " + response.statusCode());
				}
			};
		}

		public static CheckCallback checkCallback(int status, String message) {
			return new CheckCallback(status, message);
		}
		public static CheckMapCallback checkMapCallback(int status, Object...namesAndValues) {
			return new CheckMapCallback(status, namesAndValues);
		}
	}
}