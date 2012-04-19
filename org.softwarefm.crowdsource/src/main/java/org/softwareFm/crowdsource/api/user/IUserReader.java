/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.user;

import java.util.Map;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public interface IUserReader {
	<T> T getUserProperty(String userId, String userCrypto, String property);

	void refresh(String userId);

	abstract public static class Utils {
		public static String getUserProperty(IUserAndGroupsContainer container, final String softwareFmId, final String userCrypto, final String propertyName) {
			return container.accessUserReader(new IFunction1<IUserReader, String>() {
				@Override
				public String apply(IUserReader from) throws Exception {
					return from.getUserProperty(softwareFmId, userCrypto, propertyName);
				}
			}, ICallback.Utils.<String> noCallback()).get();
		}

		public static void setUserProperty(IUserAndGroupsContainer readWriteApi, final String softwareFmId, final String userCrypto, final String propertyName, final String value) {
			readWriteApi.accessUser(new ICallback<IUser>() {
				@Override
				public void process(IUser user) throws Exception {
					user.setUserProperty(softwareFmId, userCrypto, propertyName, value);
				}
			}).get();
		}

		public static IUserReader mockReader(final Object... nameAndVariables) {
			return new IUserReader() {
				private final Map<String, Object> map = Maps.makeMap(nameAndVariables);

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

		public static IUserReader exceptionUserReader() {
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

		public static void refresh(IUserAndGroupsContainer container, final String softwareFmId) {
			ITransaction<Void> transaction = container.accessUserReader(new IFunction1<IUserReader, Void>() {
				@Override
				public Void apply(IUserReader from) throws Exception {
					from.refresh(softwareFmId);
					return null;
				}
			}, ICallback.Utils.<Void> noCallback());
			transaction.get();
		}

	}
}