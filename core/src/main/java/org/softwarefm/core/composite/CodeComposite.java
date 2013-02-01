package org.softwarefm.core.composite;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.browser.BrowserAndFriendsComposite;
import org.softwarefm.core.browser.IEditingListener;
import org.softwarefm.core.constants.UrlConstants;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

public class CodeComposite extends SoftwareFmComposite {

	private final BrowserAndFriendsComposite browser;
	private String url;
	protected boolean editing;

	public CodeComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		browser = new BrowserAndFriendsComposite(getComposite(), container);
		browser.addEditingListener(new IEditingListener() {
			@Override
			public void editingState(boolean editing) {
				CodeComposite.this.editing = editing;
			}
		});
		addSelectedBindingListener(new SelectedBindingAdapter() {
			@Override
			public void codeSelectionOccured(int selectionCount, CodeData codeData) {
				if (!editing) {
					url = container.urlStrategy.classAndMethodUrl(codeData).getHttpHostAndUrl();
					browser.setUrl(url);
					System.out.println("ClassAndMethod: " + url + ", " + " editing: " + editing);
				}
			}

			public boolean isValid() {
				return !getComposite().isDisposed();
			}

			@Override
			public String toString() {
				CodeComposite codeComposite = CodeComposite.this;
				return codeComposite.getClass().getSimpleName() + "@" + System.identityHashCode(codeComposite) + " Browser: " + browser.getComposite().isDisposed() + " Url: " + url;
			}

		});
		container.socialUsage.addSocialUsageListener(new SocialUsageAdapter(getComposite()) {
			@Override
			public void codeUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
				browser.setFriendData(friendsUsage);
			}
		});
		browser.setUrl(UrlConstants.aboutCodeComposite);
	}

	@Override
	public void dispose() {
		System.out.println("CodeComposite dispose called");
		super.dispose();
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" + getUrl() + ") " + browser;
	}

	public BrowserAndFriendsComposite getBrowserForTest() {
		return browser;

	}
}
