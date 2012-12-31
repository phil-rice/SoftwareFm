package org.softwarefm.core.friends;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.miginfocom.swt.MigLayout;

import org.apache.http.HttpStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.softwarefm.core.client.AbstractMigComposite;
import org.softwarefm.core.client.IUserConnectionDetails;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.server.configurator.ConfiguratorConstants;
import org.softwarefm.shared.friend.IFriends;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.strings.Strings;

public class FriendsComposite extends AbstractMigComposite {

	private final Text friendText;
	private final List friendsList;
	private Menu menu;
	private final List friendsUsageList;
	private final IUsagePersistance persistance;
	private ISimpleMap<String, IUsageStats> friendsUsage;
	private final IUserConnectionDetails userConnectionDetails;
	private final Button sendButton;

	public FriendsComposite(Composite parent, final IUserConnectionDetails userConnectionDetails, final IFriends friends, IUsagePersistance persistance) {
		super(parent, new MigLayout("fill", "[][grow]", "[][][grow]"));
		this.userConnectionDetails = userConnectionDetails;
		this.persistance = persistance;
		final Composite composite = getComposite();
		friendText = Swts.createMigLabelAndTextForForm(composite, "Friend", "");
		friendText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				sendButton.notifyListeners(SWT.Selection, new Event());
			}
		});

		sendButton = Swts.Buttons.makeMigPushButton(composite, "Add", "split 2, span 2");
		sendButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String user = userConnectionDetails.getUser();
				String friend = getFriend();
				if (friend.length() > 0) {
					System.out.println("Adding " + friend + " as friend of " + user);
					System.out.println(userConnectionDetails.getHttpClient().post(ConfiguratorConstants.addDeleteFriendPattern, user, friend).execute());
					getFriends(user);
				}
			}
		});
		Swts.Buttons.makeMigPushButton(composite, "Refresh", "wrap").addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String user = userConnectionDetails.getUser();
				getFriends(user);
			}

		});

		friendsList = Swts.createMigList(composite, SWT.MULTI, "span 2, split 2,grow");
		friendsUsageList = Swts.createMigList(composite, SWT.NULL, "grow");

		friendsList.setMenu(menu = new Menu(friendsList));
		MenuItem deleteItem = new MenuItem(menu, SWT.NONE);
		deleteItem.setText("delete");
		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selection = friendsList.getSelection();
				if (selection != null)
					for (String sel : selection)
						System.out.println(userConnectionDetails.getHttpClient().delete(ConfiguratorConstants.addDeleteFriendPattern, userConnectionDetails.getUser(), sel).execute());
				getFriends(userConnectionDetails.getUser());
			}
		});
		getFriends(userConnectionDetails.getUser());
		friendsList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selections = friendsList.getSelection();
				friendsUsageList.removeAll();
				if (friendsUsage != null) {
					Collection<String> paths = new HashSet<String>();
					for (String friend : selections) {
						IUsageStats usageStats = friendsUsage.get(friend);
						if (usageStats != null)
							paths.addAll(usageStats.keys());
					}
					ArrayList<String> sortedPaths = new ArrayList<String>(paths);
					Collections.sort(sortedPaths);
					for (String path : sortedPaths) {
						StringBuilder builder = new StringBuilder();

						for (String friend : selections) {
							IUsageStats usageStats = friendsUsage.get(friend);
							if (usageStats != null) {
								UsageStatData statData = usageStats.get(path);
								if (statData != null) {
									if (builder.length() > 0)
										builder.append(',');
									builder.append(friend + ":" + statData.count);
								}
							}
						}
						String result = path + " -- > " + builder;
						friendsUsageList.add(result);
					}
				}
			}
		});
	}

	private void getFriends(final String user) {
		async(new Runnable() {
			@Override
			public void run() {
				final IResponse friendsListResponse = userConnectionDetails.getHttpClient().get(ConfiguratorConstants.listFriendsPattern, user).execute();
				System.out.println("FriendsList: " + friendsListResponse);
				final IResponse friendsUsageResponse = userConnectionDetails.getHttpClient().get(ConfiguratorConstants.friendsUsagePattern, user).execute();
				System.out.println("FriendsUsage: " + friendsUsageResponse);
				getComposite().getDisplay().asyncExec(new Runnable() {
					public void run() {
						friendsList.removeAll();
						if (friendsListResponse.statusCode() == HttpStatus.SC_OK)
							for (String item : Strings.splitIgnoreBlanks(friendsListResponse.asString(), ","))
								friendsList.add(item);
						if (friendsUsageResponse.statusCode() == HttpStatus.SC_OK)
							friendsUsage = persistance.parseFriendsUsage(friendsUsageResponse.asString());
						friendsUsageList.removeAll();
					}
				});
			}
		});
	}

	protected String getFriend() {
		return friendText.getText();
	}

}
