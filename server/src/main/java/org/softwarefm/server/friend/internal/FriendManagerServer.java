package org.softwarefm.server.friend.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.softwarefm.server.MySqlStrings;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.maps.SimpleMaps;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

public class FriendManagerServer implements IFriendAndFriendManager {

	private final JdbcTemplate template;

	public FriendManagerServer(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	public void add(String user, String friend) {
		template.update(MySqlStrings.insertUserFriendIntoFriends, user, friend);
	}

	public void delete(String user, String friend) {
		template.update(MySqlStrings.deleteUserFriend, user, friend);
	}

	public List<String> friendNames(String user) {
		return template.query(MySqlStrings.selectFriendsFromUser, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(MySqlStrings.friendsField);
			}
		}, user);
	}

	@Override
	public IUsageStats pathToFriendsUsage(String user, String path) {
		final Map<String, Integer> result = new HashMap<String, Integer>();
		template.query(MySqlStrings.friendTimesFromUserAndPath, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				String friend = rs.getString(MySqlStrings.friendsField);
				int times = rs.getInt(MySqlStrings.timesField);
				Maps.add(result	, friend ,times);
			}
		}, user, path);
		return IUsageStats.Utils.fromIntegerMap(result);
	}

	public ISimpleMap<String, IUsageStats> friendsUsage(String user) {
		final Map<String, Map<String, Integer>> raw = new HashMap<String, Map<String,Integer>>();
		template.query(MySqlStrings.friendPathAndTimesFromUser, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				String friend = rs.getString(MySqlStrings.friendsField);
				String path = rs.getString(MySqlStrings.pathField);
				int times = rs.getInt(MySqlStrings.timesField);
				Maps.add(raw, friend, path, times);
			}
		}, user);
		Map<String, IUsageStats> result = Maps.mapTheMap(raw, new IFunction1<Map<String, Integer>, IUsageStats>() {
			@Override
			public IUsageStats apply(Map<String, Integer> from) throws Exception {
				return IUsageStats.Utils.fromIntegerMap(from);
			}
		});
		return SimpleMaps.fromMap(result);
	}

}
