package org.softwarefm.server.configurator;

import junit.framework.TestCase;

import org.softwarefm.httpServer.IHttpRegistry;
import org.softwarefm.shared.usage.IUsagePersistance;

/** u1 has friends fr1_1, fr1_2 while u2 has friends fr2_1 */
public abstract class AbstractFriendsConfiguratorTest extends TestCase {

	abstract protected FriendsConfigurator makeConfigurator();

	protected IHttpRegistry registry;
	protected IUsagePersistance persistance = IUsagePersistance.Utils.persistance();


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		registry = IHttpRegistry.Utils.registry();
		FriendsConfigurator configurator = makeConfigurator();
		configurator.registerWith(registry);

	}

}
