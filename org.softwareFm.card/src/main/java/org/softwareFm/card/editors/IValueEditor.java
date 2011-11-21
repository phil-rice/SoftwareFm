package org.softwareFm.card.editors;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.editors.internal.StyledTextEditor;
import org.softwareFm.card.editors.internal.TextEditor;
import org.softwareFm.card.editors.internal.ValueEditorLayout;
import org.softwareFm.card.title.TitleSpec;
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
	}

}
