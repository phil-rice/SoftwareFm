package org.softwareFm.card.navigation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.internal.History;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.resources.IResourceGetter;

public class NavBar implements IHasComposite {
	private final NavBarComposite content;

	static class NavBarComposite extends Composite {

		private final History<String> history;
		private final String rootUrl;
		private final ICallback<String> callbackToGotoUrl;
		private final Button prevButton;
		private final Button nextButton;
		private final ICardDataStore cardDataStore;
		private final int height;

		public NavBarComposite(Composite parent, int height, IResourceGetter resourceGetter, ICardDataStore cardDataStore, String rootUrl, final ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.BORDER);
			this.height = height;
			this.cardDataStore = cardDataStore;
			this.rootUrl = rootUrl;
			this.callbackToGotoUrl = callbackToGotoUrl;
			history = new History<String>();
			prevButton = Swts.makePushButton(this, resourceGetter, "navBar.prev.title", new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(callbackToGotoUrl, history.prev());
					updateNextPrevButtons();
				}
			});
			nextButton = Swts.makePushButton(this, resourceGetter, "navBar.next.title", new Runnable() {
				@Override
				public void run() {
					ICallback.Utils.call(callbackToGotoUrl, history.next());
					updateNextPrevButtons();
				}
			});
			updateNextPrevButtons();
		}

		public void noteUrlHasChanged(String url) {
			if (!url.startsWith(rootUrl))
				throw new IllegalArgumentException();
			history.push(url);
			updateNextPrevButtons();
			String endOfUrl = url.substring(rootUrl.length());
			String[] fragments = endOfUrl.split("/");
			Swts.removeChildrenAfter(this, nextButton);
			String thisUrl = rootUrl;
			for (final String string : fragments)
				if (string.length() > 0) {
					String parentUrl = thisUrl;
					new NavCombo(this, cardDataStore, parentUrl, callbackToGotoUrl);
					thisUrl += "/" + string;
					new NavButton(this, thisUrl, callbackToGotoUrl);
				}
			makeNavButton(thisUrl, thisUrl, null);
			layout();
			getParent().layout();
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			int x = 0;
			for (Control control : getChildren())
				x += control.computeSize(wHint, hHint).x;
			return new Point(x + 1000, height);
		}

		@Override
		public void layout() {
			Rectangle clientArea = getClientArea();
			int x = clientArea.x;
			int y = clientArea.y;
			for (Control control : getChildren()) {
				if (control instanceof NavControl) {
					control.setLocation(x, y);
					control.pack();
					x += control.getSize().x;
				} else if (control instanceof Button) {
					control.setLocation(x, y);
					control.setSize(control.computeSize(SWT.DEFAULT, height));
					x += control.getSize().x;
				}
			}
		}

		private void makeNavButton(String parentUrl, String thisUrl, final String string) {
			final NavControl navControl = new NavControl(this, height, string, thisUrl, callbackToGotoUrl);
			cardDataStore.processDataFor(parentUrl, new ICardDataStoreCallback<Void>() {
				@Override
				public Void process(String url, Map<String, Object> result) throws Exception {
					List<String> items = Lists.newList();
					for (Entry<String, Object> entry : result.entrySet())
						if (entry.getValue() instanceof Map<?, ?>)
							items.add(entry.getKey());
					Collections.sort(items);
					navControl.setDropdownItems(items);
					return null;
				}

				@Override
				public Void noData(String url) throws Exception {
					return process(url, Collections.<String, Object> emptyMap());
				}
			});
		}

		private void updateNextPrevButtons() {
			nextButton.setEnabled(history.hasNext());
			prevButton.setEnabled(history.hasPrev());
		}

	}

	public NavBar(Composite parent, int height, String rootUrl, IResourceGetter resourceGetter, ICallback<String> callbackToGotoUrl, ICardDataStore cardDataStore) {
		content = new NavBarComposite(parent, height, resourceGetter, cardDataStore, rootUrl, callbackToGotoUrl);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public void noteUrlHasChanged(String url) {
		content.noteUrlHasChanged(url);
	}

}
