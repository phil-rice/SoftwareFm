package org.softwareFm.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.internal.UrlEditor;
import org.softwareFm.swt.editors.internal.ValueEditorLayout;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.swt.Swts.Show;
import org.softwareFm.swt.swt.Swts.Size;
import org.softwareFm.swt.title.TitleSpec;

public class UrlEditorMain {
	public static void main(String[] args) {
		Show.displayNoLayout(UrlEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				UrlEditor textEditor = new UrlEditor(from, cardConfig, "someUrl", null, "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN)));
				textEditor.getComposite().setLayout(new ValueEditorLayout());
				// Size.resizeMeToParentsSize(textEditor.getControl());
				// textEditor.content.layout();
				Swts.layoutDump(from);
				Size.resizeMeToParentsSizeWithLayout(textEditor);
				return textEditor.content;
			}
		});
	}
	
}

