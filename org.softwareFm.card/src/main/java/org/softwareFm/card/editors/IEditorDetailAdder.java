package org.softwareFm.card.editors;

import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.editors.internal.StyledTextEditorDetailAdder;
import org.softwareFm.card.editors.internal.TextEditorDetailAdder;
import org.softwareFm.card.editors.internal.UrlEditorDetailAdder;
import org.softwareFm.utilities.functions.IFunction1;

public interface IEditorDetailAdder extends IDetailAdder {

	public static class Utils {
		public static IEditorDetailAdder text() {
			return new TextEditorDetailAdder();
		}

		public static IEditorDetailAdder styledText() {
			return new StyledTextEditorDetailAdder();
		}
		public static IEditorDetailAdder url() {
			return new UrlEditorDetailAdder();
		}
		
		public static IFunction1<String, IEditorDetailAdder> defaultEditorFn(){
			IFunction1<String, IEditorDetailAdder> editorFn = new IFunction1<String, IEditorDetailAdder>() {
				@Override
				public IEditorDetailAdder apply(String editorName) throws Exception {
					if (editorName.equals("text"))
						return text();
					if (editorName.equals("url"))
						return url();
					else if (editorName.equals("styledText"))
						return styledText();
					else if (editorName.equals("none"))
						return null;
					throw new IllegalStateException(editorName);
				}
			};
			return editorFn;
	
		}
	}
}
