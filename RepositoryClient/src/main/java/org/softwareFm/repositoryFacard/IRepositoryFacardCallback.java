/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repositoryFacard;

import java.util.Map;

import org.softwareFm.httpClient.response.IResponse;

public interface IRepositoryFacardCallback {


	void process(IResponse response, Map<String, Object> data) throws Exception;
	public static class Utils{
		public static CheckRepositoryFacardCallback checkMatches(final int statusCode, final Map<String,Object> expected){
			return new CheckRepositoryFacardCallback(expected, statusCode);
		}

		public static IRepositoryFacardCallback noCallback() {
			return new IRepositoryFacardCallback() {
				@Override
				public void process(IResponse response, Map<String, Object> data) throws Exception {
				}
			};
		}
	}
}