package org.softwarefm.server.usage.internal;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.softwarefm.httpServer.AbstractHttpServerTest;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.httpServer.routes.IRouteHandler;
import org.softwarefm.shared.constants.ConfiguratorConstants;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageReporter;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.strings.Strings;
import org.softwarefm.utilities.tests.Tests;

public class UsageReporterTest extends AbstractHttpServerTest{

	private IUsageReporter reporter;
	private String actualSentStrings;
	private IUsagePersistance persistance;
	protected String actualUser;
	
	public void testReports(){
		httpServer.start();
		reporter.report("someUser", UsageTestData.statsa1b3);
		Tests.assertEquals(UsageTestData.statsa1b3,persistance.parse( actualSentStrings));
		assertEquals("someUser", actualUser);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		reporter = IUsageReporter.Utils.reporter("localhost", port);
		persistance = IUsagePersistance.Utils.persistance();
		httpServer.register(HttpMethod.POST, new IRouteHandler() {
			@Override
			public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
				actualSentStrings = Strings.unzip(EntityUtils.toByteArray(entity));
				actualUser = parameters.get(ConfiguratorConstants.userParam);
				return StatusAndEntity.ok();
				
			}
		}, ConfiguratorConstants.userPattern, ConfiguratorConstants.userParam);
	}
	
}
