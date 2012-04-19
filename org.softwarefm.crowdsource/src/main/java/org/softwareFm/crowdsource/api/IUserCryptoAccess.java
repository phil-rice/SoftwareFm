/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import java.util.Map;

import javax.sql.DataSource;

import org.softwareFm.crowdsource.api.internal.DatabaseCryptoAccess;
import org.softwareFm.crowdsource.api.server.IMagicStringForPassword;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public interface IUserCryptoAccess extends IMagicStringForPassword {

	String getCryptoForUser(String softwareFmId);

	String emailToSoftwareFmId(String softwareFmId);

	boolean changePassword(String email, String oldHash, String newHash);

	public static class Utils {
		public static IUserCryptoAccess database(final DataSource dataSource, IIdAndSaltGenerator idAndSaltGenerator) {
			return new DatabaseCryptoAccess(dataSource, idAndSaltGenerator);
		}

		public static IUserCryptoAccess mock(final String... idsAndCryptos) {
			return new IUserCryptoAccess() {
				private final Map<String, String> map = Maps.makeMap((Object[]) idsAndCryptos);

				@Override
				public boolean changePassword(String email, String oldHash, String newHash) {
					throw new UnsupportedOperationException();
				}

				@Override
				public String allowResetPassword(String emailAddress) {
					throw new UnsupportedOperationException();
				}

				@Override
				public String getCryptoForUser(String softwareFmId) {
					return map.get(softwareFmId);
				}

				@Override
				public String emailToSoftwareFmId(String softwareFmId) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}