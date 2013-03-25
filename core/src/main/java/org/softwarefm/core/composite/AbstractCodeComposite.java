package org.softwarefm.core.composite;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.browser.BrowserAndFriendsComposite;
import org.softwarefm.core.browser.IEditingListener;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.maps.ISimpleMap;

abstract public class AbstractCodeComposite extends SoftwareFmComposite {

	private final BrowserAndFriendsComposite browser;
	private String url;
	protected boolean editing;

	public AbstractCodeComposite(Composite parent, final SoftwareFmContainer<?> container, String initialUrl) {
		super(parent, container);
		getComposite().setLayout(new FillLayout());
		browser = new BrowserAndFriendsComposite(getComposite(), container);
		browser.addEditingListener(new IEditingListener() {
			@Override
			public void editingState(boolean editing) {
				AbstractCodeComposite.this.editing = editing;
			}
		});
		addSelectedBindingListener(new SelectedBindingAdapter() {
			@Override
			public void codeSelectionOccured(int selectionCount, CodeData codeData) {
				if (!editing) {
					setUrl(getClassAndMethodUrl(container, codeData).getHttpHostAndUrl());
					System.out.println("ClassAndMethod: " + url + ", " + " editing: " + editing);
				}
			}

			public boolean isValid() {
				return !getComposite().isDisposed();
			}

			@Override
			public String toString() {
				AbstractCodeComposite codeComposite = AbstractCodeComposite.this;
				return codeComposite.getClass().getSimpleName() + "@" + System.identityHashCode(codeComposite) + " Browser: " + browser.getComposite().isDisposed() + " Url: " + url;
			}

		});
		container.socialUsage.addSocialUsageListener(new SocialUsageAdapter(getComposite()) {
			@Override
			public void codeUsage(String url, UsageStatData myUsage, ISimpleMap<FriendData, UsageStatData> friendsUsage) {
				browser.setFriendData(friendsUsage);
			}
		});
		setUrl(initialUrl);
	}

	@Override
	public void dispose() {
		System.out.println("CodeComposite dispose called");
		super.dispose();
	}

	public void setUrl(String url) {
		browser.setUrl(url);
		this.url = url;
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

	abstract protected HostOffsetAndUrl getClassAndMethodUrl(final SoftwareFmContainer<?> container, CodeData codeData);
}
