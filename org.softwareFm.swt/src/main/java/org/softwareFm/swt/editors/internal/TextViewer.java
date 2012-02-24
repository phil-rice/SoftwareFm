/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.CardOutlinePaintListener;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.IHasCardConfig;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

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
			text.setBounds(bca.x + cc.editorIndentX, bca.y + cc.editorIndentY, bca.width - 2 * cc.editorIndentX, bca.height - 2 * cc.editorIndentY);

		}

	}

	static class TextViewComposite extends Composite implements IHasCardConfig {

		private CardConfig cardConfig;
		@SuppressWarnings("unused")

		public TextViewComposite(Composite parent, final ICard card, String key) {
			super(parent, SWT.NULL);
			cardConfig = card.getCardConfig();
			String cardType = card.cardType();
			Object value = card.data().get(key);
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, card);
			LineItem lineItem = new LineItem(cardType, key, value);
			String name = Functions.call(cardConfig.nameFn(), lineItem);
			new TitleWithTitlePaintListener(this, cardConfig, titleSpec, name, "");
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
			StyledText text = new StyledText(body, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
			body.setBackground(titleSpec.background);
			body.addPaintListener(new CardOutlinePaintListener(cardConfig, Callables.value(titleSpec)));
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

	
}