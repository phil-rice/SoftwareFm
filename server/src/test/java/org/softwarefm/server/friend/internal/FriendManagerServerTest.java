package org.softwarefm.server.friend.internal;

import java.util.HashSet;

import junit.framework.TestCase;

import org.softwarefm.eclipse.usage.UsageStatData;
import org.softwarefm.server.usage.internal.MysqlTestData;
import org.softwarefm.server.usage.internal.UsageMysqlCallback;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.collections.Sets;
import org.softwarefm.utilities.tests.Tests;
import org.softwarefm.utilities.time.ITime;

public class FriendManagerServerTest extends TestCase {

	private FriendManagerServer friends;
	private UsageMysqlCallback usage;

	public void testAddingFriends() {
		friends.add("u1", "fr1_1");
		friends.add("u1", "fr1_2");
		friends.add("u2", "fr2_1");
		assertEquals(Sets.makeSet("fr1_1", "fr1_2"), new HashSet<String>(friends.friendNames("u1")));
		assertEquals(Sets.makeSet("fr2_1"), new HashSet<String>(friends.friendNames("u2")));
	}

	public void testAddingAndRemovingFriends() {
		friends.add("u1", "fr1_1");
		friends.add("u1", "fr1_2");
		friends.add("u2", "fr2_1");
		friends.delete("u1", "fr1_1");
		assertEquals(Sets.makeSet("fr1_2"), new HashSet<String>(friends.friendNames("u1")));
		assertEquals(Sets.makeSet("fr2_1"), new HashSet<String>(friends.friendNames("u2")));
	}
	
	public void testAddingFriendsMultipleTimesDoesntAddDuplicates(){
		friends.add("u1", "fr1_1");
		friends.add("u1", "fr1_1");
		friends.add("u1", "fr1_1");
		friends.add("u1", "fr1_2");
		friends.add("u1", "fr1_2");
		assertEquals(1, MysqlTestData.template.queryForInt("select count(1) from friends where user=? and friend=?", "u1", "fr1_1"));
		assertEquals(1, MysqlTestData.template.queryForInt("select count(1) from friends where user=? and friend=?", "u1", "fr1_2"));
		
		
	}

	public void testUsage() throws Exception {
		friends.add("u1", "fr1_1");
		friends.add("u1", "fr1_2");
		friends.add("u2", "fr2_1");
		
		usage.process("ip1", "u1", UsageTestData.usagea2b1);
		usage.process("ip2", "fr1_1", UsageTestData.usageb1c2);
		usage.process("ip3", "fr1_2", UsageTestData.usagec1d1);
		usage.process("ip3", "fr2_1", UsageTestData.usagea1b3);

		Tests.assertMapEquals(friends.path("u1", "a"));
		Tests.assertMapEquals(friends.path("u1", "b"), "fr1_1", new UsageStatData(2));
		Tests.assertMapEquals(friends.path("u1", "c"), "fr1_1", new UsageStatData(1), "fr1_2",new UsageStatData(1));
		Tests.assertMapEquals(friends.path("u2", "a"), "fr2_1", new UsageStatData(1));
		Tests.assertMapEquals(friends.path("u2", "b"), "fr2_1", new UsageStatData(3));
		Tests.assertMapEquals(friends.path("u2", "c"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		friends = new FriendManagerServer(MysqlTestData.dataSource);
		usage = new UsageMysqlCallback(MysqlTestData.dataSource, ITime.Utils.system());
		MysqlTestData.template.execute("delete from usage_table");
		MysqlTestData.template.execute("delete from friends");
	}

}
