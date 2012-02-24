/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.internal.CardEditor;
import org.softwareFm.swt.editors.internal.CardEditorLayout;
import org.softwareFm.swt.editors.internal.SnippetEditor;
import org.softwareFm.swt.editors.internal.StyledTextEditor;
import org.softwareFm.swt.editors.internal.TextEditor;
import org.softwareFm.swt.modifiers.ICardDataModifier;
import org.softwareFm.swt.title.TitleSpec;

public interface IValueEditor extends IHasComposite {

	public static class Utils {
		public static IValueEditor textEditorWithLayout(Composite parentComposite, CardConfig cardConfig, String url, String cardType, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
			TextEditor editor = new TextEditor(parentComposite, cardConfig, url, cardType, key, value, callback, titleSpec);
			editor.getComposite().setLayout(new DataCompositeWithFooterLayout());
			return editor;
		}

		public static IValueEditor styledTextEditorWithLayout(Composite parentComposite, CardConfig cardConfig, String url, String cardType, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
			StyledTextEditor editor = new StyledTextEditor(parentComposite, cardConfig, url, cardType, key, value, callback, titleSpec);
			editor.getComposite().setLayout(new DataCompositeWithFooterLayout());
			return editor;
		}

		public static IValueEditor cardEditorWithLayout(Composite parent, CardConfig cardConfig, String title, String cardType, String url, Map<String, Object> data, ICardEditorCallback callback) {
			CardConfig withRemoveLists = cardConfig.withCardDataModifiers(Lists.append(cardConfig.cardDataModifiers, ICardDataModifier.Utils.hideCollections()));
			CardEditor editor = new CardEditor(parent, withRemoveLists, title, cardType, url, data, callback);
			editor.getComposite().setLayout(new CardEditorLayout());
			return editor;
		}

		public static IValueEditor snippetEditorWithLayout(Composite parent, CardConfig cardConfig, String title, String url, Map<String, Object> data, ICardEditorCallback callback) {
			SnippetEditor editor = new SnippetEditor(parent, cardConfig, title, url, data, callback);
			return editor;
		}
	}

}