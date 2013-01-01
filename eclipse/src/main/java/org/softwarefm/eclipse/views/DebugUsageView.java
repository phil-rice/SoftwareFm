package org.softwarefm.eclipse.views;

import java.io.File;
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
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.IFoundFriendsListener;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsageFromServerCallback;
import org.softwarefm.shared.usage.IUsageListener;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageFromServer;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.exceptions.Exceptions;
import org.softwarefm.utilities.strings.Strings;

public class DebugUsageView extends ViewPart {

	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();

	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmContainer<ITextSelection> container = SoftwareFmPlugin.getDefault().getContainer();
		final ISocialManager socialManager = container.socialManager;
		final IUsage usage = SoftwareFmPlugin.getDefault().getUsage();
		final UsageFromServer usageFromServer = container.usageFromServer;
		final Composite composite = Swts.createMigComposite(parent, SWT.NULL, new MigLayout("fill", "[][]", "[][60!][60!][grow]"), null);
		Composite buttons = Swts.createMigComposite(composite, SWT.NULL, new MigLayout("fill"), "span 2, wrap");

		final StyledText myAndFriendsCodeUsageText = Swts.createMigStyledText(composite, SWT.WRAP | SWT.READ_ONLY, "", "span2, grow,wrap");
		final StyledText myAndFriendsArtifactUsageText = Swts.createMigStyledText(composite, SWT.WRAP | SWT.READ_ONLY, "", "span2, grow,wrap");
		final StyledText leftText = Swts.createMigStyledText(composite, SWT.WRAP | SWT.READ_ONLY, "", "grow, sgx");
		final StyledText rightText = Swts.createMigStyledText(composite, SWT.WRAP | SWT.READ_ONLY, "", "grow, sgx");

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

		usage.addUsageListener(new IUsageListener() {
			@Override
			public void usageChanged() {
				try {
					final String raw = persistance.saveUsageStats(usage.getStats());
					final int rawBytes = raw.getBytes("UTF-8").length;
					final int zippedBytes = Strings.zip(raw).length;
					Swts.asyncExec(leftText, new Runnable() {
						public void run() {
							leftText.setText("Raw: " + rawBytes + " Zipped: " + zippedBytes + "\n" + raw);
							rightText.setText(socialManager.serialize());
						}
					});
				} catch (UnsupportedEncodingException e) {
					leftText.setText(Exceptions.classAndMessage(e));
				}
			}
		});
		final IUrlStrategy urlStrategy = container.urlStrategy;
		container.selectedBindingManager.addSelectedArtifactSelectionListener(new SelectedBindingAdapter() {
			@Override
			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				String url = urlStrategy.classAndMethodUrl(codeData).url;
				add(myAndFriendsCodeUsageText, url);
			}

			@Override
			public void notInAJar(File file, int selectionCount) {
				myAndFriendsArtifactUsageText.setText("");
			}

			@Override
			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				String url = urlStrategy.versionUrl(artifactData).url;
				add(myAndFriendsArtifactUsageText, url);
			}

			private void add(StyledText text, String url) {
				text.setText(url+"\n");
				for (FriendData data: socialManager.myFriends()){
					UsageStatData usageStatData = socialManager.getUsageStats(data.name).get(url);
					if (usageStatData != null)
						text.append(data.name +" " + usageStatData+"\n");
				}
			}

			@Override
			public boolean invalid() {
				return composite.isDisposed();
			}
		});
	}

	@Override
	public void setFocus() {
	}

}