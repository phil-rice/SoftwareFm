/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

public interface ITakeOnProcessor {

	/** returns the new group id */
	String createGroup(String groupName, String groupCrypto);
	
	/** Adds the user to the group, and updates the group with the users data. 
	 * @param status TODO*/
	void addExistingUserToGroup(String groupId, String groupName, String groupCryptoKey, String softwareFmId,  String email, String status);

}