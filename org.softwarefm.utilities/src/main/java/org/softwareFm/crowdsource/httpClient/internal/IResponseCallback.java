/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.httpClient.internal;

import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;

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

		public static MemoryResponseCallback memoryCallback() {
			return new MemoryResponseCallback();
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

		public static CheckMapCallback checkMapCallback(int status, Object... namesAndValues) {
			return new CheckMapCallback(status, namesAndValues);
		}

		public static IResponseCallback throwExeceptionIfFailCallback() {
			return new IResponseCallback() {
				@Override
				public void process(IResponse response) {
					if (!CommonConstants.okStatusCodes.contains(response.statusCode()))
						throw new IllegalStateException(response.toString());
				}
			};
		}
	}
}