package org.softwarefm.core.friends.internal;

import java.io.File;

import org.softwarefm.core.friends.ISocialUsage;
import org.softwarefm.core.friends.ISocialUsageListener;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.events.internal.ListenerList;
import org.softwarefm.utilities.maps.ISimpleMap;

public class SocialUsage implements ISocialUsage {

	private final ListenerList<ISocialUsageListener> listenerList;
	private final IUrlStrategy strategy;
	private final ISocialManager socialManager;

	public SocialUsage(IMultipleListenerList list, final IUrlStrategy strategy, ISelectedBindingManager<?> selectedBindingManager, final ISocialManager socialManager) {
		this.strategy = strategy;
		this.socialManager = socialManager;
		listenerList = new ListenerList<ISocialUsageListener>(list, ISocialUsageListener.class, this);
		selectedBindingManager.addSelectedArtifactSelectionListener(makeListener());
	}

	SelectedBindingAdapter makeListener() {
		SelectedBindingAdapter listener = new SelectedBindingAdapter() {
			@Override
			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				final String url = strategy.classAndMethodUrl(codeData).url;
				listenerList.fire(new ICallback<ISocialUsageListener>() {
					@Override
					public void process(ISocialUsageListener t) throws Exception {
						UsageStatData myUsage = socialManager.getUsageStatsForCode(socialManager.myName(), url);
						ISimpleMap<FriendData, UsageStatData> friendsUsage = socialManager.getFriendsCodeUsage(url);
						t.codeUsage(url, myUsage, friendsUsage);
					}
				});
			}

			@Override
			public void notInAJar(File file, int selectionCount) {
				listenerList.fire(new ICallback<ISocialUsageListener>() {
					@Override
					public void process(ISocialUsageListener t) throws Exception {
						t.noArtifactUsage();
					}
				});
			}

			@Override
			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				listenerList.fire(new ICallback<ISocialUsageListener>() {
					@Override
					public void process(ISocialUsageListener t) throws Exception {
						t.noArtifactUsage();
					}
				});
			}

			@Override
			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				final String url = strategy.projectUrl(artifactData).url;
				listenerList.fire(new ICallback<ISocialUsageListener>() {
					@Override
					public void process(ISocialUsageListener t) throws Exception {
						UsageStatData myUsage = socialManager.getUsageStatsForArtifact(socialManager.myName(), url);
						ISimpleMap<FriendData, UsageStatData> friendsUsage = socialManager.getFriendsArtifactUsage(url);
						t.artifactUsage(url, myUsage, friendsUsage);
					}
				});
			}

			@Override
			public boolean isValid() {
				return true;
			}

		};
		return listener;
	}

	@Override
	public void addSocialUsageListener(ISocialUsageListener listener) {
		listenerList.addListener(listener);
	}

	@Override
	public void removeSocialUsageListener(ISocialUsageListener listener) {
		listenerList.removeListener(listener);
	}

}