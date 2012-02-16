/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.mySoftwareFm.ILoginCallback;

public class LoginCallbackMock implements ILoginCallback {

	public final List<UserData> loggedInUserData = Lists.newList();
	public final List<String> failedEmail = Lists.newList();
	public final List<String> failedMessage = Lists.newList();

	@Override
	public void loggedIn(UserData userData) {
		loggedInUserData.add(userData);
	}

	@Override
	public void failedToLogin(String email, String message) {
		failedEmail.add(email);
		failedMessage.add(message);
	}

}