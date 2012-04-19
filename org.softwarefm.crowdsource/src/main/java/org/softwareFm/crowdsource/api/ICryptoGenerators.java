/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.utilities.crypto.Crypto;

public interface ICryptoGenerators {

	String userCrypto();

	String groupCrypto();

	String otherCrypto(String key);

	public static class Utils {
		public static ICryptoGenerators mock(final String[] userCryptos, final String[] groupCryptos) {
			return new ICryptoGenerators() {
				private int userIndex;
				private int groupIndex;

				@Override
				public String userCrypto() {
					return userCryptos[userIndex++];
				}

				@Override
				public String otherCrypto(String key) {
					throw new UnsupportedOperationException();
				}

				@Override
				public String groupCrypto() {
					return groupCryptos[groupIndex++];
				}
			};
		}

		public static ICryptoGenerators cryptoGenerators() {
			return new ICryptoGenerators() {
				@Override
				public String userCrypto() {
					return Crypto.makeKey();
				}

				@Override
				public String otherCrypto(String key) {
					return Crypto.makeKey();
				}

				@Override
				public String groupCrypto() {
					return Crypto.makeKey();
				}
			};
		}
	}
}