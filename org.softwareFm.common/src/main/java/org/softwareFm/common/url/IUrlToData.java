/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.url;

import java.util.Map;

public interface IUrlToData {

	void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback);

	static class Utils {

		public static IUrlToData errorCallback() {
			return new IUrlToData() {
				@Override
				public void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback) {
					throw new RuntimeException();
				}
			};
		}

	}

}