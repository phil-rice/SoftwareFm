/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.tests.IIntegrationTest;
import org.springframework.jdbc.core.JdbcTemplate;

public class EmailSaltRequesterTest extends TestCase implements IIntegrationTest {

	private EmailSaltRequester saltRequester;
	private BasicDataSource dataSource;
	private JdbcTemplate template;

	public void testWhenEmailPresent() {
		template.update("insert into users (email, salt) values (?,?)", "email1", "salt1");
		template.update("insert into users (email, salt) values (?,?)", "email2", "salt2");
		assertEquals("salt1", saltRequester.getSalt("email1"));
		assertEquals("salt2", saltRequester.getSalt("email2"));
	}

	public void testWhenEmailNotPresent() {
		assertNull(saltRequester.getSalt("email1"));
	}

	@Override
	protected void setUp() throws Exception {
		dataSource = AbstractLoginDataAccessor.defaultDataSource();
		saltRequester = new EmailSaltRequester(dataSource);
		template = new JdbcTemplate(dataSource);
		template.update("truncate users");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dataSource.close();
	}

}