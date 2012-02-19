/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common;

import java.util.Map;

import org.softwareFm.common.internal.LocalUserReader;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;

public interface IUserReader {
	<T> T getUserProperty(String userId, String userCrypto, String property);

	void refresh(String userId);

	abstract public static class Utils {
		public static IUserReader localUserReader(IGitLocal gitLocal, IUrlGenerator userGenerator) {
			return new LocalUserReader(gitLocal, userGenerator);
		}
		
		public static IUserReader mockReader(final Object...nameAndVariables){
			return new IUserReader() {
				private final Map<String,Object> map = Maps.makeMap(nameAndVariables);
				@Override
				public void refresh(String userId) {
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public <T> T getUserProperty(String userId, String userCrypto, String property) {
					return (T) map.get(property);
				}
			};
		}
		
		public static IUserReader exceptionUserReader(){
			return new IUserReader() {
				
				@Override
				public void refresh(String userId) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public <T> T getUserProperty(String userId, String userCrypto, String property) {
					throw new UnsupportedOperationException();
				}
			};
		}

	}
}