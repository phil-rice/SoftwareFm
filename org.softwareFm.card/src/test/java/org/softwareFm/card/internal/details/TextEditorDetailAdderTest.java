package org.softwareFm.card.internal.details;

import org.softwareFm.card.internal.editors.TextEditor;

public class TextEditorDetailAdderTest extends AbstractValueEditorDetailAdderTest<TextEditorDetailAdder, TextEditor> {

	@Override
	protected TextEditorDetailAdder makeDetailsAdder() {
		return new TextEditorDetailAdder();
	}

}
