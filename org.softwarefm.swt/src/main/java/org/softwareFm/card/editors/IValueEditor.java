/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.internal.CardEditor;
import org.softwareFm.card.editors.internal.CardEditorLayout;
import org.softwareFm.card.editors.internal.SnippetEditor;
import org.softwareFm.card.editors.internal.StyledTextEditor;
import org.softwareFm.card.editors.internal.TextEditor;
import org.softwareFm.card.editors.internal.ValueEditorLayout;
import org.softwareFm.card.modifiers.ICardDataModifier;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.display.composites.IHasComposite;

public interface IValueEditor extends IHasComposite {

	public static class Utils {
		public static IValueEditor textEditorWithLayout(Composite parentComposite, CardConfig cardConfig, String url, String cardType, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
			TextEditor editor = new TextEditor(parentComposite, cardConfig, url, cardType, key, value, callback, titleSpec);
			editor.getComposite().setLayout(new ValueEditorLayout());
			return editor;
		}

		public static IValueEditor styledTextEditorWithLayout(Composite parentComposite, CardConfig cardConfig, String url, String cardType, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
			StyledTextEditor editor = new StyledTextEditor(parentComposite, cardConfig, url, cardType, key, value, callback, titleSpec);
			editor.getComposite().setLayout(new ValueEditorLayout());
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