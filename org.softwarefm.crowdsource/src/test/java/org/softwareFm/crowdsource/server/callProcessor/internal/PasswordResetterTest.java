/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import org.softwareFm.crowdsource.api.server.AbstractLoginSignupForgotCheckerTest;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;

public class PasswordResetterTest extends AbstractLoginSignupForgotCheckerTest {

	public void testRosyView() {
		checkSignup("email1@a", "moniker", "salt", "initialHash", "sfmId1");
		String magicString = checkSendPasswordEmail("email1@a");
		String newPassword = resetPassword.reset(magicString);

		String newDigest = Crypto.digest("salt", newPassword);
		String actualNewDigest = template.queryForObject("select password from users where email =?", String.class, new Object[] { "email1@a" });
		assertEquals(newDigest, actualNewDigest);
	}

	public void testWhenMagicStringNotThere() {
		String magicString = "not in";
		String newPassword = resetPassword.reset(magicString);
		assertNull(newPassword);
	}
}