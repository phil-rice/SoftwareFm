/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.composites;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class TextInBorder implements IHasControl {

	public static IFunction1<Composite, TextInBorder> makeTextWhenKeyMightNotExist(final int textStyle, final CardConfig cardConfig, final String cardType, final String titleKey, final String bodyKey, final Object... args) {
		return new IFunction1<Composite, TextInBorder>() {
			@Override
			public TextInBorder apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setTextFromResourceGetterWhenKeyMightNotExist(cardType, titleKey, bodyKey, args);
				if (null != null)
					result.addClickedListener(null);
				return result;
			}
		};
	}

	public static IFunction1<Composite, TextInBorder> makeTextFromString(final int textStyle, final CardConfig cardConfig, final String cardType, final String titleText, final String bodyText) {
		return new IFunction1<Composite, TextInBorder>() {
			@Override
			public TextInBorder apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setText(cardType, titleText, bodyText);
				if (null != null)
					result.addClickedListener(null);
				return result;
			}
		};
	}

	public static IFunction1<Composite, TextInBorder> makeText(final int textStyle, final CardConfig cardConfig, String cardType, String titleKey, String bodyKey, Object... args) {
		return makeTextWithClick(textStyle, cardConfig, null, cardType, titleKey, bodyKey, args);
	}

	public static IFunction1<Composite, TextInBorder> makeTextWithClick(final int textStyle, final CardConfig cardConfig, final Runnable whenClicked, final String cardType, final String titleKey, final String bodyKey, final Object... args) {
		return new IFunction1<Composite, TextInBorder>() {
			@Override
			public TextInBorder apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setTextFromResourceGetter(cardType, titleKey, bodyKey, args);
				if (whenClicked != null)
					result.addClickedListener(whenClicked);
				return result;
			}
		};
	}

	private final CardConfig cardConfig;
	private final Composite content;
	private final StyledTextWithBold textWithBold;
	private final Composite body;
	private final Title title;

	public TextInBorder(Composite parent, int textStyle, final CardConfig cardConfig) {
		this.cardConfig = cardConfig;
		content = new CompositeWithCardMargin(parent, SWT.NULL, cardConfig);
		title = new Title(content, cardConfig, TitleSpec.noTitleSpec(parent.getBackground()), "", "");
		body = new Composite(content, SWT.NULL);
		content.setLayout(Swts.titleAndContentLayout(cardConfig.titleHeight));
		body.setLayout(new FillLayout());

		textWithBold = new StyledTextWithBold(body, SWT.WRAP | SWT.READ_ONLY | SWT.BORDER, cardConfig);
		content.addPaintListener(new OutlinePaintListener(cardConfig));
	}

	public void setMenu(IFunction1<Control, Menu> fn) {
		Menu menu = Functions.call(fn, textWithBold.getControl());
		textWithBold.getControl().setMenu(menu);
	}

	public void setTextFromResourceGetterWhenKeyMightNotExist(String cardType, String titleKey, String bodyKey, Object... args) {
		String titlePattern = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, cardType, titleKey);
		String titleText = titlePattern == null ? "" : MessageFormat.format(titlePattern, args);

		String bodyPattern = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, cardType, bodyKey);
		String bodyText = bodyPattern == null ? "" : MessageFormat.format(bodyPattern, args);

		setText(cardType, titleText, bodyText);
	}

	public void setTextFromResourceGetter(String cardType, String titleKey, String bodyKey, Object... args) {
		String titlePattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, titleKey);
		String titleText = MessageFormat.format(titlePattern, args);

		String bodyPattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, bodyKey);
		String bodyText = MessageFormat.format(bodyPattern, args);
		setText(cardType, titleText, bodyText);
	}

	protected void setText(final String cardType, String titleText, String bodyText) {
		textWithBold.setText(bodyText);
		TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, new ICardData() {

			@Override
			public CardConfig getCardConfig() {
				return cardConfig;
			}

			@Override
			public void valueChanged(String key, Object newValue) {
				throw new UnsupportedOperationException();
			}

			@Override
			public String url() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Map<String, Object> data() {
				throw new UnsupportedOperationException();
			}

			@Override
			public String cardType() {
				return cardType;
			}
		});
		textWithBold.setBackground(titleSpec.titleColor);
		title.setTitleAndImage(titleText, "", titleSpec);
	}

	public void addClickedListener(final Runnable whenClicked) {
		textWithBold.getControl().addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				whenClicked.run();
			}
		});
	}

	@Override
	public Control getControl() {
		return content;
	}

}