/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;


public class EmailSaltRequestIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testWithNoError() throws Exception {
		String email = "someEmail";

		String salt1 = makeSalt();
		signup(email, salt1, "someMoniker", "hash", "someNewSoftwareFmId0");
		
		String sessionSalt = makeSalt();
		String emailSalt = requestEmailSalt( sessionSalt, email);
		assertEquals(salt1, emailSalt);
	}
}