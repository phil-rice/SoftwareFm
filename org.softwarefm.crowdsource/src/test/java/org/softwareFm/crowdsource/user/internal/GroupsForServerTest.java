/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class GroupsForServerTest extends ApiTest {

	public IFunction1<String, String> repoGenerator = Strings.firstNSegments(3);
	private final String groupId = "groupId";
	private final String groupCrypto = Crypto.makeKey();

	public void testAddGetPropertyWithoutUsers() {
		checkSetGetGroups();
	}

	public void testAddUsers() {
		getServerUserAndGroupsContainer().modifyGroups(new ICallback<IGroups>() {
			@Override
			public void process(IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "someProperty", "someValue");
				groups.addUser(groupId, groupCrypto, makeUserDetails(0));
				groups.addUser(groupId, groupCrypto, makeUserDetails(1));
				groups.addUser(groupId, groupCrypto, makeUserDetails(2));
			}
		});

		checkUsers(3);
	}

	public void testRemoveUser() {
  	}

	@Override
	protected Map<String, Callable<Object>> getDefaultGroupValues() {
		Map<String, Callable<Object>> defaults = Maps.makeMap("default", Callables.valueFromList("defaultValue"));
		return defaults;
	}

	public void testGetWithDefaultPropertes() {
		useLocalReaderRemoteGroups(new ICallback2<IGroupsReader, IGroups>() {
			@Override
			public void process(IGroupsReader localGroupsReader, IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "property1", "value1");// creates group
				assertEquals("defaultValue", groups.getGroupProperty(groupId, groupCrypto, "default"));
				assertEquals("defaultValue", groups.getGroupProperty(groupId, groupCrypto, "default")); // this actually checks doesn't call default callable again, as it would run out of values

				assertNull(groups.getGroupProperty(groupId, groupCrypto, "noDefault"));

				assertEquals("defaultValue", localGroupsReader.getGroupProperty(groupId, groupCrypto, "default"));
				assertEquals("defaultValue", localGroupsReader.getGroupProperty(groupId, groupCrypto, "default")); // this actually checks doesn't call default callable again, as it would run out of values

				assertNull(localGroupsReader.getGroupProperty(groupId, groupCrypto, "noDefault"));

			}
		});

	}

	public void testAddingUsersDoesntImpactOnProperties() {
		getServerUserAndGroupsContainer().modifyGroups(new ICallback<IGroups>() {
			@Override
			public void process(IGroups groups) throws Exception {
				checkSetGetGroups();
				groups.addUser(groupId, groupCrypto, makeUserDetails(0));
				checkUsers(1);

				checkGetNewGroups();
				groups.addUser(groupId, groupCrypto, makeUserDetails(1));
				checkUsers(2);

				checkGetNewGroups();
				groups.addUser(groupId, groupCrypto, makeUserDetails(2));
				checkUsers(3);
			}
		});
	}

	public void testSettingPropertiesDoesntImpactOnUsers() {
		useLocalReaderRemoteGroups(new ICallback2<IGroupsReader, IGroups>() {
			@Override
			public void process(IGroupsReader localGroupsReader, IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "property1", "value1");
				groups.setGroupProperty(groupId, groupCrypto, "property2", "value2");
				groups.setGroupProperty(groupId, groupCrypto, "property3", "value3");

				assertEquals("value1", groups.getGroupProperty(groupId, groupCrypto, "property1"));
				assertEquals("value2", groups.getGroupProperty(groupId, groupCrypto, "property2"));
				assertEquals("value3", groups.getGroupProperty(groupId, groupCrypto, "property3"));

				assertEquals("value1", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property1"));
				assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
				assertEquals("value3", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property3"));

				groups.setGroupProperty(groupId, groupCrypto, "property2", "value2a");
				assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
				localGroupsReader.refresh(groupId);
				assertEquals("value2a", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));

				checkGetNewGroups();
				groups.addUser(groupId, groupCrypto, makeUserDetails(0));
				checkUsers(1);

				checkGetNewGroups();
				groups.addUser(groupId, groupCrypto, makeUserDetails(1));
				checkUsers(2);

				checkGetNewGroups();
				groups.addUser(groupId, groupCrypto, makeUserDetails(2));
				checkUsers(3);

			}
		});
	}

	@SuppressWarnings("unchecked")
	public void testSetUserProperty() {
		getServerUserAndGroupsContainer().modifyGroups(new ICallback<IGroups>() {
			@Override
			public void process(IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "someProperty", "someValue");
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId1"));
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId2"));
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId3"));
				groups.setUserProperty(groupId, groupCrypto, "someId2", "a", "c");
				assertEquals(Arrays.asList(makeInitialFor("someId1"), Maps.with(makeInitialFor("someId2"), "a", "c"), makeInitialFor("someId3")), Iterables.list(groups.users(groupId, groupCrypto)));
			}
		});
	}

	public void testSetUserPropertyIfNotPresent() {
		getServerUserAndGroupsContainer().modifyGroups(new ICallback<IGroups>() {
			@Override
			public void process(final IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "someProperty", "someValue");
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId1"));
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId2"));
				Tests.assertThrowsWithMessage("Error setting user property. GroupId: groupId SoftwareFmId: someId3 Property a Value c ChangedCount 0", IllegalArgumentException.class, new Runnable() {
					@Override
					public void run() {
						groups.setUserProperty(groupId, groupCrypto, "someId3", "a", "c");
					}
				});
			}
		});
	}

	public void testSetUserPropertyIfThroughCorruptDataIdInTwice() {
		getServerUserAndGroupsContainer().modifyGroups(new ICallback<IGroups>() {
			@Override
			public void process(final IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "someProperty", "someValue");
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId1"));
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId2"));
				groups.addUser(groupId, groupCrypto, makeInitialFor("someId2"));
				Tests.assertThrowsWithMessage("Error setting user property. GroupId: groupId SoftwareFmId: someId2 Property a Value c ChangedCount 2", IllegalArgumentException.class, new Runnable() {
					@Override
					public void run() {
						groups.setUserProperty(groupId, groupCrypto, "someId2", "a", "c");
					}
				});
			}
		});
	}

	protected Map<String, Object> makeInitialFor(String id) {
		Map<String, Object> initial = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, id, "a", "b");
		return initial;
	}

	public void testSetGetReport() {
		useLocalReaderRemoteGroups(new ICallback2<IGroupsReader, IGroups>() {
			@Override
			public void process(IGroupsReader localGroupsReader, IGroups groups) throws Exception {
				groups.setReport(groupId, groupCrypto, "month1", v11);
				groups.setReport(groupId, groupCrypto, "month2", v12);
				assertEquals(v11, groups.getUsageReport(groupId, groupCrypto, "month1"));
				assertEquals(v12, groups.getUsageReport(groupId, groupCrypto, "month2"));
				assertNull(groups.getUsageReport(groupId, groupCrypto, "noSuchMonth"));

				assertEquals(v11, localGroupsReader.getUsageReport(groupId, groupCrypto, "month1"));
				assertEquals(v12, localGroupsReader.getUsageReport(groupId, groupCrypto, "month2"));
			}
		});
	}

	private void checkUsers(final int userCount) {
		useLocalReaderRemoteGroups(new ICallback2<IGroupsReader, IGroups>() {
			@Override
			public void process(IGroupsReader localGroupsReader, IGroups groups) throws Exception {
				List<Map<String, Object>> expected = Lists.newList();
				for (int i = 0; i < userCount; i++)
					expected.add(makeUserDetails(i));
				assertEquals(expected, Iterables.list(groups.users(groupId, groupCrypto)));

				localGroupsReader.refresh("");
				assertEquals(expected, Iterables.list(localGroupsReader.users(groupId, groupCrypto)));
			}
		});
	}

	private Map<String, Object> makeUserDetails(long i) {
		return Maps.stringObjectMap("value", i);
	}

	protected void useLocalReaderRemoteGroups(final ICallback2<IGroupsReader, IGroups> callback) {
		getServerUserAndGroupsContainer().modifyGroups(new ICallback<IGroups>() {
			@Override
			public void process(final IGroups groups) throws Exception {
				getLocalUserAndGroupsContainer().accessGroupReader(new IFunction1<IGroupsReader, Void>() {
					@Override
					public Void apply(IGroupsReader groupsReader) throws Exception {
						callback.process(groupsReader, groups);
						return null;
					}
				});
			}
		});
	}

	protected void checkSetGetGroups() {
		useLocalReaderRemoteGroups(new ICallback2<IGroupsReader, IGroups>() {
			@Override
			public void process(IGroupsReader localGroupsReader, IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, "property1", "value1");
				groups.setGroupProperty(groupId, groupCrypto, "property2", "value2");
				groups.setGroupProperty(groupId, groupCrypto, "property3", "value3");

				assertEquals("value1", groups.getGroupProperty(groupId, groupCrypto, "property1"));
				assertEquals("value2", groups.getGroupProperty(groupId, groupCrypto, "property2"));
				assertEquals("value3", groups.getGroupProperty(groupId, groupCrypto, "property3"));

				assertEquals("value1", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property1"));
				assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
				assertEquals("value3", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property3"));

				groups.setGroupProperty(groupId, groupCrypto, "property2", "value2a");
				assertEquals("value2", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
				localGroupsReader.refresh(groupId);
				assertEquals("value2a", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
			}
		});

		checkGetNewGroups();
	}

	protected void checkGetNewGroups() {
		useLocalReaderRemoteGroups(new ICallback2<IGroupsReader, IGroups>() {
			@Override
			public void process(IGroupsReader localGroupsReader, IGroups groups) throws Exception {
				assertEquals("value1", groups.getGroupProperty(groupId, groupCrypto, "property1"));
				assertEquals("value2a", groups.getGroupProperty(groupId, groupCrypto, "property2"));
				assertEquals("value3", groups.getGroupProperty(groupId, groupCrypto, "property3"));

				assertEquals("value1", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property1"));
				assertEquals("value2a", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property2"));
				assertEquals("value3", localGroupsReader.getGroupProperty(groupId, groupCrypto, "property3"));
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getServerApi().getServer();

	}
}