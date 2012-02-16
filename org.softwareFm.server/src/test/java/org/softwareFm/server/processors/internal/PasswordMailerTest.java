/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import org.softwareFm.server.processors.AbstractLoginSignupForgotCheckerTest;

public class PasswordMailerTest extends AbstractLoginSignupForgotCheckerTest {

	public void testWhenEmailExists() {
		checkSignup("email1@a", "moniker1", "salt1", "hash1", "sfmId1");
		checkSignup("email2@b", "moniker2", "salt2", "hash2", "sfmId2");

		checkSendPasswordEmail("email1@a");
	}

	public void testWhenEmailDoesntExist() {
		checkSignup("email1@a", "moniker1", "salt1", "hash1", "sfmId1");
		checkSignup("email2@b", "moniker2", "salt2", "hash2", "sfmId2");

		checkCannotSendEMailPassword("email3@a");
	}

	
	
}