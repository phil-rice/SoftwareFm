package org.softwareFm.card.card.internal.details;

import org.softwareFm.card.editors.internal.TextEditor;
import org.softwareFm.card.editors.internal.TextEditorDetailAdder;

public class TextEditorDetailAdderTest extends AbstractValueEditorDetailAdderTest<TextEditorDetailAdder, TextEditor> {

	@Override
	protected TextEditorDetailAdder makeDetailsAdder() {
		return new TextEditorDetailAdder();
	}

}
