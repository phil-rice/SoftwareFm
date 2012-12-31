package org.softwarefm.core.usage;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.softwarefm.core.client.AbstractClientAndUserComposite;
import org.softwarefm.core.friends.FriendManagerLocal;
import org.softwarefm.core.friends.FriendsComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.shared.friend.IFriend;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.internal.Usage;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.functions.IFunction1;

public class UsageAndFriendsDebugComposite extends AbstractClientAndUserComposite{

	public UsageAndFriendsDebugComposite(Composite parent, IUsage usage, IUsagePersistance usagePersistance, IFriend friend) {
		super(parent, new MigLayout("fill", "[][grow]", "[][][][grow]"));
		TabFolder tabFolder = Swts.createMigTab(getComposite(), SWT.NULL, "span 2, grow");
		Swts.createTabItem(tabFolder, SWT.NULL, "Usage",  new UsageComposite(tabFolder, this, usage, usagePersistance).getControl());
		Swts.createTabItem(tabFolder, SWT.NULL, "Friends", new FriendsComposite(tabFolder, this, friend, usagePersistance).getControl());
	}
	
	public static void main(String[] args) {
		Swts.Show.displayFormLayout("Usage and Friends", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new UsageAndFriendsDebugComposite(from,  new Usage(IMultipleListenerList.Utils.defaultList()), IUsagePersistance.Utils.persistance(), new FriendManagerLocal()).getComposite();
			}
		});
	}

}
