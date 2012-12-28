package org.softwarefm.server.configurator;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.softwarefm.httpServer.IHttpRegistry;
import org.softwarefm.httpServer.IRegistryConfigurator;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.httpServer.routes.IRouteHandler;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.strings.Strings;

public class FriendsConfigurator implements IRegistryConfigurator {

	private final IFriendAndFriendManager manager;
	private final IUsagePersistance persistance;

	public FriendsConfigurator(IFriendAndFriendManager manager, IUsagePersistance persistance) {
		this.manager = manager;
		this.persistance = persistance;
	}

	@Override
	public void registerWith(IHttpRegistry registry) {
		registry.register(HttpMethod.GET, new IRouteHandler() {
			@Override
			public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
				String user = parameters.get(ConfiguratorConstants.userParam);
				String result = Strings.join(manager.friendNames(user), ",");
				return StatusAndEntity.ok(result);
			}
		}, ConfiguratorConstants.listFriendsPattern, ConfiguratorConstants.userParam);

		registry.register(HttpMethod.POST, new IRouteHandler() {
			@Override
			public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
				String user = parameters.get(ConfiguratorConstants.userParam);
				String friend = parameters.get(ConfiguratorConstants.friendParam);
				manager.add(user, friend);
				return StatusAndEntity.ok();
			}
		}, ConfiguratorConstants.addDeleteFriendPattern, ConfiguratorConstants.userParam, ConfiguratorConstants.friendParam);

		registry.register(HttpMethod.DELETE, new IRouteHandler() {
			@Override
			public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
				String user = parameters.get(ConfiguratorConstants.userParam);
				String friend = parameters.get(ConfiguratorConstants.friendParam);
				manager.delete(user, friend);
				return StatusAndEntity.ok();
			}
		}, ConfiguratorConstants.addDeleteFriendPattern, ConfiguratorConstants.userParam, ConfiguratorConstants.friendParam);

		registry.register(HttpMethod.GET, new IRouteHandler() {
			@Override
			public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
				String user = parameters.get(ConfiguratorConstants.userParam);
				ISimpleMap<String, IUsageStats> usage = manager.friendsUsage(user);
				String text = persistance.saveFriendsUsage(usage);
				return StatusAndEntity.ok(text, true);
			}
		}, ConfiguratorConstants.friendsUsagePattern, ConfiguratorConstants.userParam);

	}

}
