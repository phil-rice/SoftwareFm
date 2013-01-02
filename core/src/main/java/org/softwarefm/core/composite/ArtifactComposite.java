package org.softwarefm.core.composite;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.constants.UrlConstants;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.SimpleMaps;

public class ArtifactComposite extends StackedBrowserAndControl<LinkComposite> {

	public ArtifactComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container, new IFunction1<Composite, LinkComposite>() {
			public LinkComposite apply(Composite from) throws Exception {
				return new LinkComposite(from, container);
			}
		});
		setUrlAndShow(UrlConstants.aboutArtifactComposite);
		container.socialUsage.addSocialUsageListener(new SocialUsageAdapter(getControl()) {
			@Override
			public void noArtifactUsage() {
				browserAndFriendComposite.setFriendData(SimpleMaps.<FriendData, UsageStatData>empty());
			}
			@Override
			public void artifactUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
				browserAndFriendComposite.setFriendData(friendsUsage);
			}
		});
		addListener(new SelectedBindingAdapter() {
			@Override
			public void notJavaElement(int selectionCount) {
				setUrlAndShow(UrlConstants.notJavaElementUrl);
			}

			@Override
			public void notInAJar(File file, int selectionCount) {
				setUrlAndShow(UrlConstants.notJarUrl);
			}

			@Override
			public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
				setText(digestDeterminedMsg(TextKeys.msgArtifactFoundDigest, fileAndDigest) + "\n" + searchingMsg());
			}

			@Override
			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				showSecondaryControl();
			}

			@Override
			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				String url = urlStrategy.projectUrl(artifactData).getHostAndUrl();
				setUrlAndShow(url);
				container.state.setUrlSuffix(null);
			}

			public boolean invalid() {
				return getComposite().isDisposed();
			}

			@Override
			public String toString() {
				ArtifactComposite artifactComposite = ArtifactComposite.this;
				return artifactComposite.getClass().getSimpleName() + "@" + System.identityHashCode(artifactComposite) + " Url/text: " + getTextOrUrl();
			}
		});
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" + getTextOrUrl() + ")";
	}

	public static void main(String[] args) {
		Swts.Show.displayFormLayout(ArtifactComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				ArtifactComposite artifactComposite = new ArtifactComposite(from, SoftwareFmContainer.makeForTests(from.getDisplay()));
				artifactComposite.setUrlAndShow(UrlConstants.notJarUrl);
				return artifactComposite.getComposite();
			}
		});
	}

}
