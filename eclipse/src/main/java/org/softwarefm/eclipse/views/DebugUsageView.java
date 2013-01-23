package org.softwarefm.eclipse.views;

import java.io.UnsupportedEncodingException;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.friends.ISocialUsageListenerWithValid;
import org.softwarefm.core.friends.internal.SocialUsage;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.IFoundFriendsListener;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsageFromServer;
import org.softwarefm.shared.usage.IUsageFromServerCallback;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.exceptions.Exceptions;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.strings.Strings;

public class DebugUsageView extends ViewPart {

	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
	private Composite composite;

	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmContainer<ITextSelection> container = SoftwareFmPlugin.getDefault().getContainer();
		final ISocialManager socialManager = container.socialManager;
		final IUsage usage = SoftwareFmPlugin.getDefault().getUsage();
		final IUsageFromServer usageFromServer = container.usageFromServer;
		SocialUsage socialUsage = container.socialUsage;
		composite = Swts.createMigComposite(parent, SWT.NULL, new MigLayout("fill", "[]5![]", "[]2![60!]2![60!]2![grow]"), null);
		Composite buttons = Swts.createMigComposite(composite, SWT.NULL, new MigLayout("fill"), "span 2, wrap");

		final StyledText myAndFriendsCodeUsageText = Swts.createMigReadOnlyStyledText(composite, "", "span2, grow,wrap");
		final StyledText myAndFriendsArtifactUsageText = Swts.createMigReadOnlyStyledText(composite, "", "span2, grow,wrap");
		final StyledText leftText = Swts.createMigReadOnlyStyledText(composite, "", "grow, sgx");
		final StyledText rightText = Swts.createMigReadOnlyStyledText(composite, "", "grow, sgx");

		Swts.Buttons.makeMigPushButton(buttons, "Refresh", "", new Listener() {
			@Override
			public void handleEvent(Event event) {
				new Thread() {
					@Override
					public void run() {
						IUsageFromServerCallback callback = new IUsageFromServerCallback() {
							@Override
							public void foundStats(String name, IUsageStats usageStats) {
								socialManager.setUsageData(name, usageStats);
							}
						};
						List<FriendData> myFriends = socialManager.myFriends();
						usageFromServer.getStatsFor(myFriends, callback);
						usageFromServer.getStatsFor(socialManager.myName(), callback);
						Swts.asyncExec(rightText, new Runnable() {
							@Override
							public void run() {
								rightText.setText(socialManager.serialize());
							}
						});
					}
				}.start();
			}
		});
		Swts.Buttons.makeMigButtonForTest(buttons, "SendUsage", new Listener() {
			@Override
			public void handleEvent(Event event) {
				SoftwareFmPlugin.getDefault().sendUsage();
			}
		}, "");
		socialManager.addFoundFriendsListener(new IFoundFriendsListener() {
			@Override
			public void foundFriends(List<FriendData> friends) {
				rightText.setText(socialManager.serialize());
			}
		});
		rightText.setText(socialManager.serialize());

		socialUsage.addSocialUsageListener(new ISocialUsageListenerWithValid() {
			@Override
			public void noArtifactUsage() {
				myAndFriendsArtifactUsageText.setText("");
			}

			@Override
			public void codeUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
				add(myAndFriendsCodeUsageText, url, myUsage, friendsUsage);
			}

			@Override
			public void artifactUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
				add(myAndFriendsArtifactUsageText, url, myUsage, friendsUsage);
			}

			@Override
			public boolean isValid() {
				return !composite.isDisposed();
			}

			private void add(StyledText text, String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
				text.setText(url + "\n");
				text.append("My Usage: " + myUsage + "\n");
				for (int i = 0; i < friendsUsage.size(); i++) {
					FriendData data = friendsUsage.key(i);
					UsageStatData usageStatData = friendsUsage.get(data);
					text.append(data.name + " " + usageStatData + ", ");
				}
			}

		});

		container.selectedBindingManager.addSelectedArtifactSelectionListener(new SelectedBindingAdapter() {
			@Override
			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				try {
					final String raw = persistance.saveUsageStats(usage.getStats());
					final int rawBytes = raw.getBytes("UTF-8").length;
					final int zippedBytes = Strings.zip(raw).length;
					Swts.asyncExec(leftText, new Runnable() {
						@Override
						public void run() {
							leftText.setText("Raw: " + rawBytes + " Zipped: " + zippedBytes + "\n" + raw);
							rightText.setText(socialManager.serialize());
						}
					});
				} catch (UnsupportedEncodingException e) {
					leftText.setText(Exceptions.classAndMessage(e));
				}
			}

			@Override
			public boolean isValid() {
				return !composite.isDisposed();
			}
		});
	}

	@Override
	public void setFocus() {
		composite.setFocus();
	}

}