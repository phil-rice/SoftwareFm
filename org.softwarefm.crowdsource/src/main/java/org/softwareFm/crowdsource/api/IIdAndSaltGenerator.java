/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import java.text.MessageFormat;
import java.util.UUID;

public interface IIdAndSaltGenerator {

	String makeNewUserId();

	String makeNewGroupId();

	String makeSalt();

	String makeMagicString();

	public static class Utils {
		public static IIdAndSaltGenerator uuidGenerators() {
			return new IIdAndSaltGenerator() {
				@Override
				public String makeNewUserId() {
					return UUID.randomUUID().toString();
				}

				@Override
				public String makeSalt() {
					return UUID.randomUUID().toString();
				}

				@Override
				public String makeNewGroupId() {
					return UUID.randomUUID().toString();
				}

				@Override
				public String makeMagicString() {
					return UUID.randomUUID().toString();
				}
			};
		}

		public static IIdAndSaltGenerator mockGenerators(final String userPattern, final String groupPattern, final String saltPattern, final String magicStringPattern) {
			return new IIdAndSaltGenerator() {
				int userIndex;
				int groupIndex;
				int saltIndex;
				int magicStringIndex;

				@Override
				public String makeNewUserId() {
					return MessageFormat.format(userPattern, userIndex++);
				}

				@Override
				public String makeSalt() {
					return MessageFormat.format(groupPattern, saltIndex++);
				}

				@Override
				public String makeNewGroupId() {
					return MessageFormat.format(groupPattern, groupIndex++);
				}

				@Override
				public String makeMagicString() {
					return MessageFormat.format(magicStringPattern, magicStringIndex++);
				}
			};
		}
	}
}