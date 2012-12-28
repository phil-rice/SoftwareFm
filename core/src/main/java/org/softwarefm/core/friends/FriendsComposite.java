package org.softwarefm.core.friends;

import java.text.MessageFormat;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.server.configurator.ConfiguratorConstants;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.strings.Strings;

public class FriendsComposite extends HasComposite {

	private final Text hostText;
	private final Text portText;
	private final Text userText;
	private final Text friendText;
	private final List friendsListText;
	private Menu menu;

	public FriendsComposite(Composite parent) {
		super(parent);
		Composite composite = getComposite();
		composite.setLayout(new MigLayout("fill", "[][grow]", "[][][][][grow]"));
		hostText = Swts.createMigLabelAndTextForForm(composite, "Host", "localhost");
		portText = Swts.createMigLabelAndTextForForm(composite, "Port", "8082");
		userText = Swts.createMigLabelAndTextForForm(composite, "User", "me");
		friendText = Swts.createMigLabelAndTextForForm(composite, "Friend", "");
		friendText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				String user = getUser();
				String friend = getFriend();
				if (friend.length() > 0) {
					System.out.println("Adding " + friend + " as friend of " + user);
					System.out.println(IHttpClient.Utils.builder().host(getHost(), getPort()).post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, user, friend)).execute());
					getFriends(user);

				}
			}
		});
		friendsListText = Swts.createMigStyledList(composite, SWT.NULL, "grow,spanx 2");
		friendsListText.setMenu(menu = new Menu(friendsListText));
		MenuItem deleteItem = new MenuItem(menu, SWT.NONE);
		deleteItem.setText("delete");
		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selection = friendsListText.getSelection();
				if (selection != null)
					for (String sel : selection)
						System.out.println(IHttpClient.Utils.builder().host(getHost(), getPort()).delete(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, getUser(), sel)).execute());
				getFriends(getUser());
			}
		});
		getFriends(getUser());
	}

	private void getFriends(String user) {
		IResponse friendsListResponse = IHttpClient.Utils.builder().host(getHost(), getPort()).get(MessageFormat.format(ConfiguratorConstants.listFriendsPattern, user)).execute();
		System.out.println(friendsListResponse);
		friendsListText.removeAll();
		for (String item : Strings.splitIgnoreBlanks(friendsListResponse.asString(), ","))
			friendsListText.add(item);
	}

	protected String getHost() {
		return hostText.getText();
	}

	protected int getPort() {
		return Integer.parseInt(portText.getText());
	}

	protected String getFriend() {
		return friendText.getText();
	}

	protected String getUser() {
		return userText.getText();
	}

	public static void main(String[] args) {
		Swts.Show.display("FriendsComposite", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new FriendsComposite(from).getComposite();
			}
		});
	}
}
