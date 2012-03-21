/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.user;

import java.util.List;
import java.util.Map;

/** These method modify the files that represent groups. On the client, these methods make take some significant time as multiple client/server round trips may be involved */
public interface IGroups extends IGroupsReader {

	void setGroupProperty(String groupId, String groupCryptoKey, String property, Object value);

	void setUserProperty(String groupId, String groupCrypto, String softwareFmId, String property, String value);

	void addUser(String groupId, String groupCryptoKey, Map<String, Object> userDetails);

	void removeUsers(String groupId, String groupCryptoKey, List<String> softwareFmId);

	void setReport(String groupId, String groupCryptoKey, String month, Map<String, Object> report);

}