/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.user;

import java.util.Map;

import org.softwareFm.crowdsource.api.ICrowdSourcedReaderApi;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

/**
 * This is the reader for information about groups of people
 * 
 * The group file is a set of lines, each line encrypted with the groupCryptoKey. If you are a member of the group, you have the key. This is done so that changes to the group data are "git friendly". Adding another user required another line, changing a users or the group's properties is again only one line. This means that git can send deltas for only the line
 * 
 * The first line is the encrypted value of a map of group properties
 * 
 * Each line after the first is data for a user *
 * 
 * @author Phil
 * 
 */
public interface IGroupsReader {


	<T> T getGroupProperty(String groupId, String groupCryptoKey, String propertyName);

	/** Iterates through the users one at a time. The map is of the form {softwareFmId -> "someSoftwareFmId", email -> "someEmail", "moniker"->"someMoniker", status -> "admin/member/invited/requesting"}. Email or monikor are optional */
	Iterable<Map<String, Object>> users(String groupId, String groupCryptoKey);

	/** pulls the latest data about the group from the server */
	void refresh(String groupId);

	Map<String, Object> getUsageReport(String groupId, String groupCryptoKey, String month);

	int membershipCount(String groupId,String groupCryptoKey);
	public static class  Utils {

		public static String getGroupProperty(ICrowdSourcedReaderApi readerApi, final String groupId, final String groupCrypto, final String propertyName) {
			return readerApi.accessGroupReader(new IFunction1<IGroupsReader, String>() {
				@Override
				public String apply(IGroupsReader from) throws Exception {
					return from.getGroupProperty(groupId, groupCrypto, propertyName);
				}
			});
		}
		
	}
}