package org.softwarefm.server.configurator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.server.MySqlStrings;
import org.softwarefm.server.MysqlTestData;
import org.softwarefm.server.usage.internal.UsageMysqlCallbackAndGetter;
import org.softwarefm.shared.constants.ConfiguratorConstants;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.tests.Tests;
import org.softwarefm.utilities.time.ITime;
import org.springframework.jdbc.core.RowMapper;

public class UsageConfiguratorWithMysqlTest extends AbstractUsageConfiguratorTest {

	public void testSetAndGet() throws Exception{
		checkSet();
		checkGet();
		List<String> actual = MysqlTestData.template.query(MySqlStrings.selectFromUsage, new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(MySqlStrings.pathField) + rs.getInt(MySqlStrings.timesField);
			}}, "someUser");
		Tests.assertEqualsAsSet(Arrays.asList("a1","b3"), actual);
	}
	
	public void testSetTwiceFollowedByGetAggregatesResults() throws Exception{
		checkSet();
		checkSet();
		StatusAndEntity statusAndEntity = registry.process(HttpMethod.GET, MessageFormat.format(ConfiguratorConstants.userPattern, "someUser"), null);
		assertEquals(HttpStatus.SC_OK, statusAndEntity.status);
		assertEquals(persistance.saveUsageStats(UsageTestData.statsa2b6), EntityUtils.toString(statusAndEntity.entity));
		List<String> actual = MysqlTestData.template.query(MySqlStrings.selectFromUsage, new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(MySqlStrings.pathField) + rs.getInt(MySqlStrings.timesField);
			}}, "someUser");
		Tests.assertEqualsAsSet(Arrays.asList("a1","a1", "b3", "b3"), actual);
		
	}
	
	@Override
	protected UsageConfigurator makeConfigurator() {
		UsageMysqlCallbackAndGetter callbackAndGetter = new UsageMysqlCallbackAndGetter(MysqlTestData.dataSource, ITime.Utils.system());
		return new UsageConfigurator(callbackAndGetter, callbackAndGetter, persistance);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MysqlTestData.template.execute("delete from usage_table");
	}
}
