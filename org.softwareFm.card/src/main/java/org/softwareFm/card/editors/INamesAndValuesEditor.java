package org.softwareFm.card.editors;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.editors.internal.NameAndValuesEditor;
import org.softwareFm.utilities.functions.IFunction1;

public interface INamesAndValuesEditor extends IValueEditor, ICardData {

	Composite getButtonComposite();

	public static class Utils {

		public static INamesAndValuesEditor editor(Composite parent, CardConfig cardConfig, String cardType, String title, String url, Map<String, Object> initialData, List<NameAndValueData> nameAndValueData, ICardEditorCallback callback) {
			return new NameAndValuesEditor(parent, cardConfig, cardType, title, url, initialData, nameAndValueData, callback);
		}

		public static Label label(Composite composite, final CardConfig cardConfig, final String cardType, final String key) {
			String name = cardConfig.nameFn.apply(cardConfig, new LineItem(cardType, key, ""));
			Label result = new Label(composite, SWT.NULL);
			result.setText(name);
			return result;
		}

		public static NameAndValueData text(final CardConfig cardConfig, final String cardType, final String key) {
			return new NameAndValueData(key, new IFunction1<Composite, Control>() {
				@Override
				public Control apply(Composite from) throws Exception {
					Text text = new Text(from, SWT.NULL);
					String initialValue = cardConfig.valueFn.apply(cardConfig, new LineItem(cardType, key, ""));
					text.setText(initialValue);
					return text;
				}
			});
		}
		public static NameAndValueData password(final CardConfig cardConfig, final String cardType, final String key) {
			return new NameAndValueData(key, new IFunction1<Composite, Control>() {
				@Override
				public Control apply(Composite from) throws Exception {
					Text text = new Text(from, SWT.NULL);
					text.setEchoChar('#');
					String initialValue = cardConfig.valueFn.apply(cardConfig, new LineItem(cardType, key, ""));
					text.setText(initialValue);
					return text;
				}
			});
		}

		public static NameAndValueData styledText(final CardConfig cardConfig, final String cardType, final String key) {
			return new NameAndValueData(key, new IFunction1<Composite, Control>() {
				@Override
				public Control apply(Composite from) throws Exception {
					StyledText text = new StyledText(from, SWT.V_SCROLL | SWT.WRAP );
					String initialValue = cardConfig.valueFn.apply(cardConfig, new LineItem(cardType, key, ""));
					text.setText(initialValue);
					return text;
				}
			});
		}

	}

}