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
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class TextInBorder implements IHasControl {

	public static IFunction1<Composite, TextInBorder> makeTextWhenKeyMightNotExist(final int textStyle, final CardConfig cardConfig, final String cardType, final String key, final Object... args) {
		return new IFunction1<Composite, TextInBorder>() {
			@Override
			public TextInBorder apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setTextFromResourceGetterWhenKeyMightNotExist(cardType, key, args);
				if (null != null)
					result.addClickedListener(null);
				return result;
			}
		};
	}

	public static IFunction1<Composite, TextInBorder> makeTextFromString(final int textStyle, final CardConfig cardConfig, final String cardType, final String text) {
		return new IFunction1<Composite, TextInBorder>() {
			@Override
			public TextInBorder apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setText(cardType, text);
				if (null != null)
					result.addClickedListener(null);
				return result;
			}
		};
	}

	public static IFunction1<Composite, TextInBorder> makeText(final int textStyle, final CardConfig cardConfig, String cardType, final String key, final Object... args) {
		return makeTextWithClick(textStyle, cardConfig, null, cardType, key, args);
	}

	public static IFunction1<Composite, TextInBorder> makeTextWithClick(final int textStyle, final CardConfig cardConfig, final Runnable whenClicked, final String cardType, final String key, final Object... args) {
		return new IFunction1<Composite, TextInBorder>() {
			@Override
			public TextInBorder apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setTextFromResourceGetter(cardType, key, args);
				if (whenClicked != null)
					result.addClickedListener(whenClicked);
				return result;
			}
		};
	}

	private final CardConfig cardConfig;
	private final Composite content;
	private final StyledTextWithBold textWithBold;

	public TextInBorder(Composite parent, int textStyle, final CardConfig cardConfig) {
		this.cardConfig = cardConfig;
		content = new CompositeWithCardMargin(parent, SWT.NULL, cardConfig);
		content.setLayout(new FillLayout());
		textWithBold = new StyledTextWithBold(content, SWT.WRAP | SWT.READ_ONLY, cardConfig);
		content.addPaintListener(new OutlinePaintListener(cardConfig));
	}

	public void setMenu(IFunction1<Control, Menu> fn) {
		Menu menu = Functions.call(fn, textWithBold.getControl());
		textWithBold.getControl().setMenu(menu);
	}

	public void setTextFromResourceGetterWhenKeyMightNotExist(String cardType, String patternKey, Object... args) {
		String pattern = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, cardType, patternKey);
		String text = pattern == null ? "" : MessageFormat.format(pattern, args);
		setText(cardType, text);
	}

	public void setTextFromResourceGetter(String cardType, String patternKey, Object... args) {
		String pattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, patternKey);
		String text = MessageFormat.format(pattern, args);
		setText(cardType, text);
	}

	protected void setText(final String cardType, String text) {
		textWithBold.setText(text);
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
		textWithBold.setBackground(titleSpec.background);
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
