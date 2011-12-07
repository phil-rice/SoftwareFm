package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.card.internal.CardOutlinePaintListener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.strings.Strings;

public class TextViewer implements IHasComposite {

	private final TextViewComposite content;

	public static class TextViewLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			return new Point(0, 0);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			Control[] children = composite.getChildren();
			assert children.length == 2;
			IHasCardConfig hasCardConfig = (IHasCardConfig) composite;
			CardConfig cc = hasCardConfig.getCardConfig();
			int titleHeight = cc.titleHeight + cc.topMargin;
			Rectangle ca = composite.getClientArea();
			Control title = children[0];
			title.setBounds(ca.x, ca.y, ca.width, titleHeight);
			Composite body = (Composite) children[1];
			body.setBounds(ca.x, ca.y + titleHeight, ca.width, ca.height - titleHeight);
			Control[] bodyChildren = body.getChildren();
			assert bodyChildren.length == 1;
			Control text = bodyChildren[0];
			Rectangle bca = body.getClientArea();
			text.setBounds(bca.x+cc.editorIndentX, bca.y+cc.editorIndentY, bca.width-2*cc.editorIndentX, bca.height-2*cc.editorIndentY);
			
		}

	}

	static class TextViewComposite extends Composite implements IHasCardConfig {

		private CardConfig cardConfig;

		public TextViewComposite(Composite parent, final ICard card, String key) {
			super(parent, SWT.NULL);
			cardConfig = card.getCardConfig();
			String cardType = card.cardType();
			Object value = card.data().get(key);
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, card);
			LineItem lineItem = new LineItem(cardType, key, value);
			String name = Functions.call(cardConfig.nameFn(), lineItem);
			new Title(this, cardConfig, titleSpec, name, "");
			Composite body = new Composite(this, SWT.NULL) {
				@Override
				public Rectangle getClientArea() {
					Rectangle clientArea = super.getClientArea();
					int cardWidth = clientArea.width - cardConfig.rightMargin - cardConfig.leftMargin;
					int cardHeight = clientArea.height - cardConfig.bottomMargin - 2;
					Rectangle result = new Rectangle(clientArea.x + cardConfig.leftMargin, clientArea.y + 2, cardWidth, cardHeight);
					return result;
				}
			};
			StyledText text = new StyledText(body, SWT.V_SCROLL | SWT.WRAP|SWT.BORDER|SWT.READ_ONLY);
			body.setBackground(titleSpec.background);
			body.addPaintListener(new CardOutlinePaintListener(titleSpec, cardConfig));
			text.setText(Strings.nullSafeToString(value));
		}

		@Override
		public CardConfig getCardConfig() {
			return cardConfig;
		}

	}

	public TextViewer(Composite parent, ICard card, String key) {
		content = new TextViewComposite(parent, card, key);
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
		Swts.Show.display(TextViewer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				final SashForm sashForm = new SashForm(from, SWT.HORIZONTAL);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				ICard card = ICardFactory.Utils.createCardWithLayout(sashForm, cardConfig, CardDataStoreFixture.url1a, CardDataStoreFixture.data1a);
				final Composite right = new Composite(sashForm, SWT.NULL);
				right.setLayout(new FillLayout());
				sashForm.setWeights(new int[] { 1, 2 });
				card.addLineSelectedListener(new ILineSelectedListener() {
					@Override
					public void selected(ICard card, String key, Object value) {
						Swts.removeAllChildren(right);
						TextViewer textViewer = new TextViewer(right, card, key);
						textViewer.getComposite().setLayout(new TextViewLayout());
						right.layout();
						sashForm.layout();
					}
				});
				TextViewer textViewer = new TextViewer(right, card, "1a");
				textViewer.getComposite().setLayout(new TextViewLayout());
				return sashForm;
			}
		});
	}
}
