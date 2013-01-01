package org.softwarefm.core.browser;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.friends.FriendData;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.strings.Strings;

public class BrowserAndFriendsComposite extends BrowserComposite {

	public final static String startOfFriendContainer = "<div class=\"user-relationship-container\">";
	public final static String endOfFriendsContainer = "<div class=\"cleared\"></div>";
	public final static String siteUrl = "http://data.softwarefm.com";
	public final static String defaultImageUrlPattern = siteUrl + "{0}";
	public final static String userUrlPattern = siteUrl + "/wiki/User:{0}";

	private ToolBar friendsToolBar;
	private final ImageRegistry imageRegistry;
	private final IMultipleListenerList listenerList;
	private String lastName;
	protected List<FriendData> lastResult;

	public BrowserAndFriendsComposite(Composite parent, SoftwareFmContainer<?> container) {
		this(parent, container, defaultImageUrlPattern);
	}

	public BrowserAndFriendsComposite(Composite parent, SoftwareFmContainer<?> container, final String imageUrlPattern) {
		super(parent, container);
		imageRegistry = container.imageRegistry;
		listenerList = container.listenerList;
		browser.addLocationListener(new FriendsAndNameLocationListener(imageUrlPattern));
		addFoundFriendsListener(new IFoundFriendsListener() {
			@Override
			public void foundFriends(List<FriendData> friends) {
				setFriendData(friends);
			}
		});
	}

	public void addFoundNameListener(IFoundNameListener listener) {
		listenerList.addListener(this, IFoundNameListener.class, listener);
	}

	public void removeFoundNameListener(IFoundNameListener listener) {
		listenerList.removeListener(this, IFoundNameListener.class, listener);
	}

	public void addFoundFriendsListener(IFoundFriendsListener listener) {
		listenerList.addListener(this, IFoundFriendsListener.class, listener);
	}

	public void removeFoundFriendsListener(IFoundFriendsListener listener) {
		listenerList.removeListener(this, IFoundFriendsListener.class, listener);
	}

	@Override
	protected void addMoreControls() {
		friendsToolBar = new ToolBar(rowComposite, SWT.NULL);
		friendsToolBar.moveAbove(browserUrlCombo.getControl());
	}

	public void setFriendData(List<FriendData> friendsData) {
		try {
			Swts.removeAllChildren(friendsToolBar);
			if (friendsData.size() == 0) {

			} else
				for (final FriendData data : friendsData) {
					ToolItem toolitem = new ToolItem(friendsToolBar, SWT.NULL);
					toolitem.addListener(SWT.Selection, new Listener() {
						@Override
						public void handleEvent(Event event) {
							setUrl(MessageFormat.format(userUrlPattern, data.name));
						}
					});
					toolitem.setToolTipText(data.name);

					String imageUrl = data.imageUrl;
					if (imageUrl != null) {
						Image image = imageRegistry.get(imageUrl);
						if (image == null) {
							ImageDescriptor createFromURL = ImageDescriptor.createFromURL(new URL(imageUrl));
							int height = createFromURL.getImageData().height;
							int width = createFromURL.getImageData().width;
							int scaledWidth = 16 * width / height;
							ImageData scaled = createFromURL.getImageData().scaledTo(scaledWidth, 16);
							image = new Image(getComposite().getDisplay(), scaled);
							imageRegistry.put(imageUrl, image);
						}
						System.out.println("Setting image: " + image);
						toolitem.setImage(image);
					}
				}
			for (Control control : friendsToolBar.getChildren()) {
				if (control instanceof Label) {
					Image image = ((Label) control).getImage();
					int width = image.getImageData().width;
					control.setLayoutData(new RowData(width, 16));
				}
			}
			friendsToolBar.layout();
			rowComposite.layout();
			Swts.layoutDump(rowComposite);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	private void friendsChanged(final List<FriendData> result) {
		if (!result.equals(lastResult)) {
			lastResult = result;
			listenerList.fire(BrowserAndFriendsComposite.this, IFoundFriendsListener.class, new ICallback<IFoundFriendsListener>() {
				@Override
				public void process(IFoundFriendsListener t) throws Exception {
					t.foundFriends(result);
				}
			});
		}
	}

	private class FriendsAndNameLocationListener implements LocationListener {

		private final String imageUrlPattern;

		private FriendsAndNameLocationListener(String imageUrlPattern) {
			this.imageUrlPattern = imageUrlPattern;
		}

		@Override
		public void changing(LocationEvent event) {
		}

		@Override
		public void changed(LocationEvent event) {
			if (event.location.startsWith(siteUrl)) {
				String newName = myName();
				if (!Strings.safeEquals(newName, lastName)) {
					lastName = newName;
					friendsChanged(Collections.<FriendData> emptyList());
					listenerList.fire(BrowserAndFriendsComposite.this, IFoundNameListener.class, new ICallback<IFoundNameListener>() {
						@Override
						public void process(IFoundNameListener t) throws Exception {
							t.foundName(lastName);
						}
					});
				}
			}
			if (lastName != null && event.location.equals("http://data.softwarefm.com/wiki/User:" + lastName)) {
				String html = getHtml();
				final List<FriendData> result = new ArrayList<FriendData>();
				String container = Strings.findItem(html, startOfFriendContainer, endOfFriendsContainer);
				AtomicInteger index = new AtomicInteger();
				if (container != null) {
					while (true) {
						String name = Strings.findItem(container, "User:", "\"", index);
						String image = Strings.findItem(container, " src=\"", "\"", index);
						if (name == null)
							break;
						result.add(new FriendData(name, image == null ? null : MessageFormat.format(imageUrlPattern, image)));
					}
					friendsChanged(result);
				}
			}
		}

		public String myName() {
			String html = getHtml();
			String container = Strings.findItem(html, "<div id=\"p-personal", "</div>");
			if (container != null) {
				String name = Strings.findItem(container, "User:", "\"");
				return name;
			}
			return null;
		}
	}

	public static void main(String[] args) {
		Swts.Show.display(BrowserAndFriendsComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(from.getDisplay());
				BrowserAndFriendsComposite browserComposite = new BrowserAndFriendsComposite(from, container);
				browserComposite.setUrl("www.google.com");
				return browserComposite.getComposite();
			}
		});
	}
}
