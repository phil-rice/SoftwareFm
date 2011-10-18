package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.resources.IResourceGetter;

public class NavBar implements IHasComposite {
	private final NavBarComposite content;

	static class NavControl extends Button {

		public NavControl(Composite parent, String title, final String url, final ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.PUSH);
			setText(title);
			addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ICallback.Utils.call(callbackToGotoUrl, url);
				}
			});

		}

		@Override
		protected void checkSubclass() {
		}

	}

	static class NavBarComposite extends Composite {

		private final History<String> history;
		private final String rootUrl;
		private final IResourceGetter resourceGetter;
		private final ICallback<String> callbackToGotoUrl;
		private final Button prevButton;
		private final Button nextButton;

		public NavBarComposite(Composite parent, IResourceGetter resourceGetter, int style, String rootUrl, final ICallback<String> callbackToGotoUrl) {
			super(parent, style | SWT.BORDER);
			this.resourceGetter = resourceGetter;
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
					thisUrl += "/" + string;
					new NavControl(this, string, thisUrl, callbackToGotoUrl);
				}
			setLayout(Swts.getHorizonalMarginRowLayout(3));
			getParent().layout();
			pack();
		}

		private void updateNextPrevButtons() {
			nextButton.setEnabled(history.hasNext());
			prevButton.setEnabled(history.hasPrev());
		}

	}

	public NavBar(Composite parent, String rootUrl, IResourceGetter resourceGetter, ICallback<String> callbackToGotoUrl) {
		content = new NavBarComposite(parent, resourceGetter, SWT.NULL, rootUrl, callbackToGotoUrl);
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
