package org.softwarefm.server.usage.internal;

import java.sql.Date;

import junit.framework.TestCase;

import org.softwarefm.server.usage.IUsageCallback;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.time.ITime;
import org.springframework.jdbc.core.JdbcTemplate;

public class UsageMysqlCallbackTest extends TestCase {

	private JdbcTemplate template;
	private IUsageCallback callback;
	private Date date;

	public void testUpdatesUsageInDatabase() throws Exception {
		callback.process("someIp", "someUser", UsageTestData.statsa2b1);
		assertEquals(2, template.queryForInt("select count(*) from usage_table"));
		assertEquals("someIp", template.queryForObject("select ip from usage_table where user=? and path=?", String.class, "someUser", "a"));
		assertEquals(2, template.queryForInt("select times from usage_table where user=? and path=?", "someUser", "a"));
		assertEquals(1, template.queryForInt("select times from usage_table where user=? and path=?", "someUser", "b"));
		assertEquals(date.getTime(), template.queryForObject("select time from usage_table where user=? and path=?", Date.class, "someUser", "b").getTime());
	}

	public void testAppends() throws Exception {
		callback.process("someIp", "someUser", UsageTestData.statsa2b1);
		callback.process("someIp", "someUser", UsageTestData.statsa2b1);
		callback.process("someIp", "someUser", UsageTestData.statsa2b1);
		assertEquals(6, template.queryForInt("select count(*) from usage_table"));
		assertEquals(3, template.queryForInt("select count(*) from usage_table where user=? and path=? and ip=?", "someUser", "a", "someIp"));
		assertEquals(3, template.queryForInt("select count(*) from usage_table where user=? and path=? and times=?", "someUser", "a", 2));
		assertEquals(3, template.queryForInt("select count(*) from usage_table where user=? and path=? and times=?", "someUser", "b", 1));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		template = new JdbcTemplate(MysqlTestData.dataSource);
		template.update("delete from usage_table");
		date = new Date(2012, 10, 12);
		callback = IUsageCallback.Utils.mySqlCallback(MysqlTestData.dataSource, ITime.Utils.dummy(date));
	}

}
