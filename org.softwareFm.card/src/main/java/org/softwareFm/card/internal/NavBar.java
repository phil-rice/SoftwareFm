package org.softwareFm.card.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.resources.IResourceGetter;

public class NavBar implements IHasComposite {
	private final NavBarComposite content;

	static class NavControl extends Composite {

		private final Combo combo;
		private final String title;

		public NavControl(Composite parent, final String title, final String url, final ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.PUSH);
			this.title = title;
			combo = new Combo(this, SWT.DROP_DOWN);
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selectionIndex = combo.getSelectionIndex();
					if (selectionIndex == -1) {
						return;
					}
					String postFix = combo.getItem(selectionIndex);
					if (!postFix.equals(title))
						select(url, callbackToGotoUrl, postFix);
				}

				private void select(final String url, final ICallback<String> callbackToGotoUrl, String postFix) {
					System.out.println("select: " + url);
					int index = url.lastIndexOf('/');
					String prefix = index == -1 ? "" : url.substring(0, index);
					String newUrl = prefix + "/" + postFix;
					pack();
					getParent().layout();
					ICallback.Utils.call(callbackToGotoUrl, newUrl);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					select(url, callbackToGotoUrl, combo.getText());

				}
			});
			setLayout(new FillLayout());
		}

		@Override
		protected void checkSubclass() {
		}

		public void setDropdownItems(List<String> items) {
			String text = combo.getText();
			combo.removeAll();
			for (int i = 0; i < items.size(); i++) {
				String item = items.get(i);
				combo.add(item);
			}
			combo.setText(title);
			combo.pack();
			pack();
			layout();
			getParent().pack();
			getParent().layout();
			if (items.size()==0)
				combo.setEnabled(false);
			combo.forceFocus();
		}

	}

	static class NavBarComposite extends Composite {

		private final History<String> history;
		private final String rootUrl;
		private final ICallback<String> callbackToGotoUrl;
		private final Button prevButton;
		private final Button nextButton;
		private final ICardDataStore cardDataStore;

		public NavBarComposite(Composite parent, IResourceGetter resourceGetter, ICardDataStore cardDataStore, int style, String rootUrl, final ICallback<String> callbackToGotoUrl) {
			super(parent, style | SWT.BORDER);
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
					thisUrl += "/" + string;
					makeNavButton(parentUrl, thisUrl, string);
				}
			makeNavButton(thisUrl, thisUrl + "/<junk>", "<Select>");
			setLayout(new GridLayout(getChildren().length, false));
			layout();
			getParent().layout();
		}

		private void makeNavButton(String parentUrl, String thisUrl, final String string) {
			final NavControl navControl = new NavControl(this, string, thisUrl, callbackToGotoUrl);
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

	public NavBar(Composite parent, String rootUrl, IResourceGetter resourceGetter, ICallback<String> callbackToGotoUrl, ICardDataStore cardDataStore) {
		content = new NavBarComposite(parent, resourceGetter, cardDataStore, SWT.NULL, rootUrl, callbackToGotoUrl);
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
