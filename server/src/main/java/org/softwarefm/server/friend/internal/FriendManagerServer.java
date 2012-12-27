package org.softwarefm.server.friend.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.eclipse.usage.UsageStatData;
import org.softwarefm.server.MySqlStrings;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.utilities.maps.ISimpleMap;
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


	public IUsageStats path(String user, String path) {
		final Map<String, UsageStatData> result = new HashMap<String, UsageStatData>();
		template.query(MySqlStrings.friendTimesFromUserPath, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				String friend = rs.getString(MySqlStrings.friendsField);
				int times = rs.getInt(MySqlStrings.timesField);
				result.put(friend, new UsageStatData(times));
			}
		}, user, path);
		return IUsageStats.Utils.fromMap(result);
	}

	public ISimpleMap<String, IUsageStats> friendsUsage(String user) {
		return null;
	}

}
