/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import java.util.Map;

import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public interface ITakeOnEnrichmentProvider {

	Map<String, Object> takeOn(Map<String, Object> initial, String userCrypto, IUserAndGroupsContainer container);

	public static class Utils {
		public static ITakeOnEnrichmentProvider noEnrichment() {
			return new ITakeOnEnrichmentProvider() {
				@Override
				public Map<String, Object> takeOn(Map<String, Object> initial, String userCrypto, IUserAndGroupsContainer container) {
					return initial;
				}
			};
		}

		public static ITakeOnEnrichmentProvider enrichmentWithUserProperty(final String propertyName) {
			return new ITakeOnEnrichmentProvider() {
				@Override
				public Map<String, Object> takeOn(Map<String, Object> initial, final String userCrypto, IUserAndGroupsContainer container) {
					final String userId = (String) initial.get(LoginConstants.softwareFmIdKey);
					String propertyValue = container.accessUserReader(new IFunction1<IUserReader, String>() {
						@Override
						public String apply(IUserReader from) throws Exception {
							return from.getUserProperty(userId, userCrypto, propertyName);
						}
					}, ICallback.Utils.<String> noCallback()).get();
					return Maps.with(initial, propertyName, propertyValue);
				}
			};
		}
	}
}