package org.softwarefm.server;

public class MySqlStrings {

	public static final String insertUserFriendIntoFriends = "insert into friends (user,friend) values (?,?) on duplicate key update friend=friend";
	public static final String deleteUserFriend = "delete from  friends where user=? and friend=?";
	public static final String selectFriendsFromUser = "select friend from friends where user=?";
	public static final String friendTimesFromUserPath = "select f.friend, u.times from usage_table u, friends f where u.user = f.friend and f.user = ? and u.path = ?";
	public static final String insertIntoUsage = "insert into usage_table (ip, user, path, times, time) values(?,?,?,?,?)";
	// fields
	public static final String timesField = "times";
	public static final String friendsField = "friend";

}
