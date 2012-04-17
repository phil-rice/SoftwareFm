/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.navigation.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.navigation.IRepoNavigation;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.history.History;
import org.softwareFm.crowdsource.utilities.history.IHistory;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.navigation.ITitleBarForCard;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.TitlePaintListener;
import org.softwareFm.swt.title.TitleSpec;

public class NavBar implements IHasComposite, ITitleBarForCard {

	public static class NavBarLayout extends Layout {

		@Override
		public Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			NavBarComposite nav = (NavBarComposite) composite;
			Rectangle clientArea = nav.getClientArea();
			int x = clientArea.x;
			for (Control control : nav.getChildren())
				if (control instanceof Combo) {
					x += nav.cardConfig.navIconWidth;
				} else
					x += control.computeSize(wHint, hHint).x;
			return new Point(x, nav.height);
		}

		@Override
		public void layout(Composite composite, boolean flushCache) {
			NavBarComposite nav = (NavBarComposite) composite;
			Rectangle ca = nav.getClientArea();
			// System.out.println(" NavBar " + " " + ca + "  parent " + nav.getParent().getBounds());
			Control[] children = nav.getChildren();
			nav.navNextHistoryPrev.layout();
			nav.navNextHistoryPrev.getControl().setLocation(ca.x, ca.y);
			for (Control control : children) {
				if (control instanceof Combo) {
					control.setSize(nav.cardConfig.navIconWidth, nav.height);
				} else {
					int width = control.computeSize(SWT.DEFAULT, ca.height).x;
					control.setSize(width, ca.height);
				}
			}
			int i = 1;
			if (isTooBig(nav, ca)) {
				while (isTooBig(nav, ca) && i < children.length) {
					Control child = children[i];
					if (child instanceof Label) {
						child.setSize(nav.cardConfig.compressedNavTitleWidth, nav.height);
						child.setToolTipText(((Label) child).getText());
					}
					i++;
				}
			}
			int x = ca.x;
			int y = ca.y + 2;
			for (Control control : nav.getChildren())
				if (control instanceof Combo) {
					control.setLocation(x, y);
					x += nav.cardConfig.navIconWidth;
				} else {
					control.setLocation(x, y);
					int width = control.getSize().x;
					x += width;
				}

		}

		private boolean isTooBig(NavBarComposite nav, Rectangle clientArea) {
			int width = clientArea.x;
			for (Control child : nav.getChildren())
				width += child.getSize().x;
			return width >= clientArea.x + clientArea.width;
		}

	}

	static class NavBarComposite extends Composite {

		private final List<String> rootUrls;
		private final ICallback<String> callbackToGotoUrl;
		private final int height;
		private final CardConfig cardConfig;
		private String url;
		private final TitlePaintListener listener;
		private final NavNextHistoryPrev<String> navNextHistoryPrev;
		private final IContainer container;

		public NavBarComposite(Composite parent, CardConfig cardConfig, IContainer container, List<String> rootUrls, final ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.NULL);
			this.container = container;
			this.height = cardConfig.titleHeight;
			this.cardConfig = cardConfig;
			this.rootUrls = rootUrls;
			this.callbackToGotoUrl = callbackToGotoUrl;
			navNextHistoryPrev = new NavNextHistoryPrev<String>(this, new NavNextHistoryPrevConfig<String>(height, cardConfig.imageFn, Functions.<String> toStringFn(), callbackToGotoUrl), new History<String>());
			navNextHistoryPrev.setLayout(new NavNextHistoryPrev.NavNextHistoryPrevLayout<String>());
			listener = new TitlePaintListener(cardConfig, TitleSpec.noTitleSpec(getBackground()), "");
			addPaintListener(listener);
		}

		@SuppressWarnings("unused")
		public ITransaction<Map<String,List<String>>> setUrl(final String url, TitleSpec titleSpec) {
			this.url = url;
			navNextHistoryPrev.getHistory().push(url);
			listener.setTitleAndTitleSpec("", titleSpec.withoutImage());
			// setBackground(titleSpec.background);
			final String root = Strings.oneStartsWith(rootUrls, url);
			if (root == null)
				throw new IllegalArgumentException("rooturl: " + rootUrls + " url: " + url);
			navNextHistoryPrev.setBackground(titleSpec.titleColor);
			final String endOfUrl = url.substring(root.length());
			List<String> urlFragments = Strings.splitIgnoreBlanks(endOfUrl, "/");
			Swts.removeChildrenAfter(this, navNextHistoryPrev.getControl());
			String thisUrl = root;
			final List<NavCombo> combos = Lists.newList();
			for (final String string : urlFragments)
				if (string.length() > 0) {
					String parentUrl = thisUrl;
					NavCombo e = new NavCombo(this, cardConfig, parentUrl, string, callbackToGotoUrl);
					combos.add(e);
					thisUrl += "/" + string;
					new NavButton(this, thisUrl, callbackToGotoUrl);
				}
			combos.add(new NavCombo(this, cardConfig, url, "", callbackToGotoUrl));
			for (Control control : getChildren())
				control.setBackground(titleSpec.titleColor);
			layout();
			redraw();
			return container.accessWithCallbackFn(IRepoNavigation.class, new IFunction1<IRepoNavigation, Map<String, List<String>>>() {
				@Override
				public Map<String, List<String>> apply(IRepoNavigation repoNavigation) throws Exception {
					Map<String, List<String>> map = repoNavigation.navigationData(url, ICallback.Utils.<Map<String, List<String>>> noCallback()).get();
					return map;
				}
			}, new ISwtFunction1<Map<String, List<String>>, Map<String, List<String>>>() {
				@Override
				public Map<String, List<String>> apply(Map<String, List<String>> from) throws Exception {
					List<String> urls = Lists.addAtStart(Urls.urlsToRoot(endOfUrl),"");
					assert urls.size() == combos.size() : "Urls: " + urls + ", Combos: " + combos;
					for (int i = 0; i < urls.size(); i++) {
						String thisUrl = urls.get(i);
						String rootAndUrl = Urls.compose(root, thisUrl);
						List<String> value = Maps.getOrDefault(from, rootAndUrl, Collections.<String> emptyList());
						NavCombo navCombo = combos.get(i);
						navCombo.setDropdownItems(value);
					}
					return from;
				}
			});
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle clientArea = super.getClientArea();
			return new Rectangle(clientArea.x + cardConfig.leftMargin, clientArea.y + cardConfig.topMargin, clientArea.width - listener.getTitleSpec().rightIndent - cardConfig.leftMargin - cardConfig.rightMargin, clientArea.height - cardConfig.topMargin);
		}
	}

	private final NavBarComposite content;

	public NavBar(Composite parent, CardConfig cardConfig, IContainer container, List<String> rootUrls, ICallback<String> callbackToGotoUrl) {
		content = new NavBarComposite(parent, cardConfig, container, rootUrls, callbackToGotoUrl);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public List<String> getRootUrls() {
		return Collections.unmodifiableList(content.rootUrls);
	}

	public String getCurrentUrl() {
		return content.url;
	}

	public IHistory<String> getHistory() {
		return content.navNextHistoryPrev.getHistory();
	}

	@Override
	public ITransaction<Map<String, List<String>>> setUrl(ICard card) {
		TitleSpec titleSpec = Functions.call(card.getCardConfig().titleSpecFn, card);
		return content.setUrl(card.url(), titleSpec);
	}

	public NavNextHistoryPrev<String> getNavHistoryPrev() {
		return content.navNextHistoryPrev;
	}

}