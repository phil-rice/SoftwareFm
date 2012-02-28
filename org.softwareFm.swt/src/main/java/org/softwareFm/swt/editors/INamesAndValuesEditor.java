/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;

public interface INamesAndValuesEditor extends IValueEditor, ICardData {

	Composite getButtonComposite();

	public static class Utils {

		public static INamesAndValuesEditor editor(Composite parent, CardConfig cardConfig, String cardType, String title, String url, Map<String, Object> initialData, List<KeyAndEditStrategy> keyAndEditStrategy, ICardEditorCallback callback) {
			return new NameAndValuesEditor(parent, cardConfig, cardType, title, url, initialData, keyAndEditStrategy, callback);
		}

		public static Label label(Composite composite, final CardConfig cardConfig, final String cardType, final KeyAndEditStrategy data) {
			String name = data.displayLabel ? cardConfig.nameFn.apply(cardConfig, new LineItem(cardType, data.key, "")) : "";
			Label result = new Label(composite, SWT.NULL);
			result.setText(name);
			return result;
		}

		public static KeyAndEditStrategy text(CardConfig cardConfig, String key) {
			return new KeyAndEditStrategy(key, IEditableControlStrategy.Utils.text());
		}

		public static KeyAndEditStrategy styledText(CardConfig cardConfig, String key) {
			return new KeyAndEditStrategy(key, IEditableControlStrategy.Utils.styledText(SWT.WRAP));
		}

		public static KeyAndEditStrategy message(CardConfig cardConfig, String key) {
			return new KeyAndEditStrategy(false, key, IEditableControlStrategy.Utils.message());
		}

		public static KeyAndEditStrategy password(CardConfig cardConfig, String key) {
			return new KeyAndEditStrategy(key, IEditableControlStrategy.Utils.password());
		}

	}

}