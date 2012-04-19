/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.crowdsource.server.doers.internal.MagicStringForPassword;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.springframework.jdbc.core.JdbcTemplate;

public class MagicStringForPasswordTest extends TestCase {

	private MagicStringForPassword magicStringForPassword;
	private JdbcTemplate template;
	private final String email = "someEmail";

	public void testGeneratesNewPasswordResetKeyWhenNoneSet() {
		template.update("insert into users(email) values(?)", email);
		assertEquals("string1", magicStringForPassword.allowResetPassword(email));
		assertEquals("string1", magicStringForPassword.allowResetPassword(email));
	}

	public void testReusesNewPasswordResetKeyWhenSet() {
		template.update("insert into users(email,passwordResetKey) values(?,?)", email, "thisValue");
		assertEquals("thisValue", magicStringForPassword.allowResetPassword(email));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		template = new JdbcTemplate(dataSource);
		magicStringForPassword = new MagicStringForPassword(dataSource, Callables.valueFromList("string1", "string2"));
		template.update("delete from users");
	}
}