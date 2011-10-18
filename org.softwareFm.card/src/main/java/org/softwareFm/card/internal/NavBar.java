package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.resources.IResourceGetter;

public class NavBar implements IHasComposite {
	private final NavBarComposite content;

	static class NavBarComposite extends Composite {

		private final History<String> history;
		private final String rootUrl;
		private final IResourceGetter resourceGetter;
		private final ICallback<String> callbackToGotoUrl;

		public NavBarComposite(Composite parent, IResourceGetter resourceGetter, int style, String rootUrl, ICallback<String> callbackToGotoUrl) {
			super(parent, style);
			this.resourceGetter = resourceGetter;
			this.rootUrl = rootUrl;
			this.callbackToGotoUrl = callbackToGotoUrl;
			history = new History<String>();
		}

		public void noteUrlHasChanged(String url) {
			if (!url.startsWith(rootUrl))
				throw new IllegalArgumentException();
			history.push(url);
			String endOfUrl = url.substring(rootUrl.length());
			String[] fragments = endOfUrl.split("/");
			Swts.removeAllChildren(this);
			for (String string : fragments)
				Swts.makePushButton(this, resourceGetter, string, false, new Runnable() {
					@Override
					public void run() {
						System.out.println("PressedButton");
					}
				});
			setLayout(Swts.getHorizonalMarginRowLayout(3));
			getParent().layout();
			pack();
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
