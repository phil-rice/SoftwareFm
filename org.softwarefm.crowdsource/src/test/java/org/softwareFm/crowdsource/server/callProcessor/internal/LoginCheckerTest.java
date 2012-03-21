/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import org.softwareFm.crowdsource.api.server.AbstractLoginSignupForgotCheckerTest;

public class LoginCheckerTest extends AbstractLoginSignupForgotCheckerTest {

	public void testLoginWhenExists() {
		String crypto1 = checkSignup("email1", "moniker1", "salt1", "hash1", "sfmId1");
		String crypto2 = checkSignup("email2", "moniker2", "salt2", "hash2", "sfmId2");
		checkLogin("email1", "hash1", crypto1, "sfmId1");
		checkLogin("email2", "hash2", crypto2, "sfmId2");
	}

	public void testLoginWhenDoesntExists() {
		String crypto1 = checkSignup("email1", "moniker1", "salt1", "hash1", "sfmId1");
		checkSignup("email2", "moniker2", "salt2", "hash2", "sfmId2");

		checkCannotLogin("email1", "unknown");
		checkLogin("email1", "hash1", crypto1, "sfmId1");
	}

}