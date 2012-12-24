package org.softwarefm.eclipse.usage.internal;

import java.sql.Date;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwarefm.eclipse.usage.IUsageCallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.time.ITime;
import org.springframework.jdbc.core.JdbcTemplate;

public class UsageMysqlCallbackTest extends TestCase {

	private final static BasicDataSource dataSource = new BasicDataSource();
	static {
		dataSource.setUrl("jdbc:mysql://localhost:3306/softwarefm");
		dataSource.setUsername("root");
		dataSource.setPassword("iwtbde");
	}
	private JdbcTemplate template;
	private Usage usagea2b1;
	private Usage usageb1c2;
	private IUsageCallback callback;
	private Date date;

	public void testUpdatesUsageInDatabase() throws Exception {
		callback.process("someIp", "someUser", usagea2b1);
		assertEquals(2, template.queryForInt("select count(*) from usage_table"));
		assertEquals("someIp", template.queryForObject("select ip from usage_table where user=? and path=?", String.class, "someUser", "a"));
		assertEquals(2, template.queryForInt("select times from usage_table where user=? and path=?", "someUser", "a"));
		assertEquals(1, template.queryForInt("select times from usage_table where user=? and path=?", "someUser", "b"));
		assertEquals(date.getTime(), template.queryForObject("select time from usage_table where user=? and path=?", Date.class, "someUser", "b").getTime());
	}

	public void testAppends() throws Exception {
		callback.process("someIp", "someUser", usagea2b1);
		callback.process("someIp", "someUser", usagea2b1);
		callback.process("someIp", "someUser", usagea2b1);
		assertEquals(6, template.queryForInt("select count(*) from usage_table"));
		assertEquals(3, template.queryForInt("select count(*) from usage_table where user=? and path=? and ip=?", "someUser", "a", "someIp"));
		assertEquals(3, template.queryForInt("select count(*) from usage_table where user=? and path=? and times=?", "someUser", "a", 2));
		assertEquals(3, template.queryForInt("select count(*) from usage_table where user=? and path=? and times=?", "someUser", "b", 1));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		usagea2b1 = new Usage(IMultipleListenerList.Utils.defaultList());
		usagea2b1.selected("a");
		usagea2b1.selected("a");
		usagea2b1.selected("b");
		usageb1c2 = new Usage(IMultipleListenerList.Utils.defaultList());
		usageb1c2.selected("b");
		usageb1c2.selected("b");
		usageb1c2.selected("c");
		template = new JdbcTemplate(dataSource);
		template.update("delete from usage_table");
		date = new Date(2012, 10, 12);
		callback = IUsageCallback.Utils.mySqlCallback(dataSource, ITime.Utils.dummy(date));
	}

}
