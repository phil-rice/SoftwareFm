package org.softwareFm.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.internal.TextEditor;
import org.softwareFm.swt.editors.internal.ValueEditorLayout;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.swt.Swts.Show;
import org.softwareFm.swt.swt.Swts.Size;
import org.softwareFm.swt.title.TitleSpec;

public class TextEditorMain {
	public static void main(String[] args) {
		Show.displayNoLayout(TextEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				TextEditor TextEditor = new TextEditor(from, cardConfig, "someUrl", null, "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN)));
				TextEditor.getComposite().setLayout(new ValueEditorLayout());
				// Size.resizeMeToParentsSize(textEditor.getControl());
				// textEditor.content.layout();
				Swts.layoutDump(from);
				Size.resizeMeToParentsSizeWithLayout(TextEditor);
				return TextEditor.content;
			}
		});
	}
	
	
}

