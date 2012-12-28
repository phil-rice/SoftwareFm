package org.softwarefm.server.configurator;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import org.softwarefm.httpServer.IHttpRegistry;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.server.configurator.ConfiguratorConstants;
import org.softwarefm.server.configurator.UsageConfigurator;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.strings.Strings;

public abstract class AbstractUsageConfiguratorTest extends TestCase {

	abstract protected UsageConfigurator makeConfigurator();

	protected IHttpRegistry registry;
	protected IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
	protected ByteArrayEntity entityWithZippedStats;
	protected String unzipped;
	protected IUsageStats stats;


	protected void checkSet() throws Exception {
		StatusAndEntity statusAndEntity = registry.process(HttpMethod.POST, MessageFormat.format(ConfiguratorConstants.userPattern, "someUser"), entityWithZippedStats);
		assertEquals(HttpStatus.SC_OK, statusAndEntity.status);
		assertEquals(null, statusAndEntity.entity);
	}
	
	protected void checkGet() throws Exception{
		StatusAndEntity statusAndEntity = registry.process(HttpMethod.GET, MessageFormat.format(ConfiguratorConstants.userPattern, "someUser"), null);
		assertEquals(HttpStatus.SC_OK, statusAndEntity.status);
		assertEquals(unzipped, EntityUtils.toString(statusAndEntity.entity));
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		registry = IHttpRegistry.Utils.registry();
		UsageConfigurator configurator = makeConfigurator();
		configurator.registerWith(registry);
		stats = UsageTestData.statsa1b3;
		unzipped = persistance.saveUsageStats(stats);
		byte[] zipped = Strings.zip(unzipped);
		entityWithZippedStats = new ByteArrayEntity(zipped);

	}

}
