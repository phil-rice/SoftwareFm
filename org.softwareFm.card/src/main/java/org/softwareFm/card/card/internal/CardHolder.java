/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.ICardHolderForTests;
import org.softwareFm.card.card.ICardSelectedListener;
import org.softwareFm.card.card.IHasCard;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.ITitleBarForCard;
import org.softwareFm.card.navigation.internal.NavBar;
import org.softwareFm.card.navigation.internal.NavTitle;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardHolder implements ICardHolderForTests {

	public static class CardHolderLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			CardHolderComposite cardHolder = (CardHolderComposite) composite;
			ICard card = cardHolder.card;
			CardConfig cardConfig = cardHolder.getCardConfig();
			int cardHeightHint = hHint == SWT.DEFAULT ? hHint : hHint - cardConfig.titleHeight;
			Point titleSize = cardHolder.title.getControl().computeSize(wHint, cardConfig.titleHeight);
			Point cardSize = card == null ? new Point(0, 0) : card.getControl().computeSize(wHint, cardHeightHint);
			return new Point(Math.max(cardSize.x, titleSize.x) + cardConfig.leftMargin + cardConfig.rightMargin, cardSize.y + cardConfig.topMargin + cardConfig.bottomMargin + cardConfig.titleHeight);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			CardHolderComposite cardHolder = (CardHolderComposite) composite;
			Rectangle clientArea = cardHolder.getClientArea();
			CardConfig cardConfig = cardHolder.getCardConfig();
			int titleHeight = cardConfig.titleHeight;
			if (cardHolder.card != null) {
				Rectangle cardBounds = new Rectangle(clientArea.x, clientArea.y + titleHeight + cardConfig.topMargin, clientArea.width, clientArea.height - titleHeight - cardConfig.topMargin);
				cardHolder.card.getControl().setBounds(cardBounds);
				cardHolder.card.getComposite().layout();
			}

			cardHolder.title.getControl().setBounds(clientArea.x, clientArea.y, clientArea.width, titleHeight + cardConfig.topMargin);
			if (cardHolder.title instanceof IHasComposite)
				((IHasComposite) cardHolder.title).getComposite().layout();
		}

	}

	static class CardHolderComposite extends Composite implements IHasCard {

		final ITitleBarForCard title;
		ICard card;
		private final CardConfig navBarCardConfig;
		private final List<ILineSelectedListener> lineListeners = new CopyOnWriteArrayList<ILineSelectedListener>();
		private final List<ICardSelectedListener> cardSelectedListeners = new CopyOnWriteArrayList<ICardSelectedListener>();

		public CardHolderComposite(Composite parent, CardConfig navBarCardConfig, List<String> rootUrls, ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.NULL);
			this.navBarCardConfig = navBarCardConfig;
			if (navBarCardConfig == null)
				throw new NullPointerException();
			if (callbackToGotoUrl == null) {
				String loadingText = IResourceGetter.Utils.getOrException(navBarCardConfig.resourceGetterFn, null, CardConstants.cardHolderLoadingText);
				title = new NavTitle(this, navBarCardConfig, TitleSpec.noTitleSpec(parent.getBackground()), loadingText, "");
				title.getControl().addMouseListener(new MouseAdapter() {
					@Override
					public void mouseUp(MouseEvent e) {
						for (ICardSelectedListener listener : cardSelectedListeners)
							listener.cardSelected(card.url());
					}
				});
			} else {
				NavBar bar = new NavBar(this, navBarCardConfig, rootUrls, callbackToGotoUrl);
				bar.getComposite().setLayout(new NavBar.NavBarLayout());
				title = bar;
			}
		}

		@Override
		public ICard getCard() {
			return card;
		}

		private CardConfig getCardConfig() {
			CardConfig cardConfig = card == null ? navBarCardConfig : card.getCardConfig();
			return cardConfig;
		}

		public void setCard(ICard card) {
			if (this.card == card)
				return;
			if (this.card != null)
				this.card.getComposite().dispose();
			try {
				if (card.getCardConfig() == null)
					throw new NullPointerException();
				this.card = card;
				card.getComposite().setLayout(new Card.CardLayout());
				title.setUrl(card);
				layout();

				card.addLineSelectedListener(new ILineSelectedListener() {
					@Override
					public void selected(ICard card, String key, Object value) {
						notifyListSelectedListeners(card, key, value);
					}
				});
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public void notifyListSelectedListeners(ICard card, String key, Object value) {
			for (ILineSelectedListener listener : lineListeners)
				listener.selected(card, key, value);
		}
	}

	private final List<ICardChangedListener> cardChangedListeners = new CopyOnWriteArrayList<ICardChangedListener>();
	final CardHolderComposite content;
	private final List<String> rootUrls;

	public CardHolder(Composite parent, CardConfig cardConfig, List<String> rootUrls, ICallback<String> callbackToGotoUrl) {
		this.rootUrls = rootUrls;
		content = new CardHolderComposite(parent, cardConfig, rootUrls, callbackToGotoUrl);
	}

	@Override
	public List<String> getRootUrls() {
		return Collections.unmodifiableList(rootUrls);
	}

	@Override
	public ICard getCard() {
		return content.card;
	}

	@Override
	public void setCard(final ICard card) {
		content.setCard(card);
		for (ICardChangedListener listener : cardChangedListeners) {
			listener.cardChanged(this, card);
			card.addValueChangedListener(listener);
		}

	}

	@Override
	public void addCardChangedListener(ICardChangedListener listener) {
		cardChangedListeners.add(listener);
	}

	@Override
	public void addCardTitleSelectedListener(ICardSelectedListener listener) {
		content.cardSelectedListeners.add(listener);
	}

	@Override
	public void addLineSelectedListener(ILineSelectedListener listener) {
		content.lineListeners.add(listener);
	}

	public ITitleBarForCard getTitle() {
		return content.title;

	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		Show.display(CardHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = CardDataStoreFixture.asyncCardConfig(from.getDisplay());
				final ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(from, cardConfig, Arrays.asList(CardDataStoreFixture.url), ICallback.Utils.<String> noCallback());
				final Future<ICard> future = ICardFactory.Utils.makeCard(cardHolder, cardConfig, CardDataStoreFixture.url1a, new ICallback<ICard>() {
					@Override
					public void process(ICard card) throws Exception {
						if (card == null)
							return;
						cardHolder.setCard(card);
					}
				});
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							throw WrappedException.wrap(e);
						}
						((GatedMockFuture<?, ?>) future).kick();
					}
				}.start();
				Size.resizeMeToParentsSize(cardHolder.getControl());
				return cardHolder.getComposite();
			}
		});
	}

	@Override
	public CardConfig getCardConfig() {
		return content.navBarCardConfig;
	}

}