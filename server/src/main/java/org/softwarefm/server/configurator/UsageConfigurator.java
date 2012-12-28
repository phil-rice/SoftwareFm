package org.softwarefm.server.configurator;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.softwarefm.httpServer.IHttpRegistry;
import org.softwarefm.httpServer.IRegistryConfigurator;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.httpServer.routes.IRouteHandler;
import org.softwarefm.server.usage.IUsageCallback;
import org.softwarefm.server.usage.IUsageGetter;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.strings.Strings;

public class UsageConfigurator implements IRegistryConfigurator {

	private final IUsageGetter getter;
	private final IUsagePersistance persistance;
	private final IUsageCallback callback;

	public UsageConfigurator(IUsageGetter getter, IUsageCallback callback, IUsagePersistance persistance) {
		super();
		this.getter = getter;
		this.callback = callback;
		this.persistance = persistance;
	}

	@Override
	public void registerWith(IHttpRegistry registry) {
		registry.register(HttpMethod.GET, new IRouteHandler() {
			@Override
			public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
				String user = parameters.get(ConfiguratorConstants.userParam);
				IUsageStats stats = getter.getStats(user);
				String text = persistance.saveUsageStats(stats);
				return StatusAndEntity.ok(text);
			}
		}, ConfiguratorConstants.userPattern, ConfiguratorConstants.userParam);
		registry.register(HttpMethod.POST, new IRouteHandler() {
			@Override
			public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
				String user = parameters.get(ConfiguratorConstants.userParam);
				byte[] zipped = EntityUtils.toByteArray(entity);
				String unzipped = Strings.unzip(zipped);
				IUsageStats stats = persistance.parse(unzipped);
				callback.process("", user, stats);
				return StatusAndEntity.ok();
			}
		}, ConfiguratorConstants.userPattern, ConfiguratorConstants.userParam);

	}

}
