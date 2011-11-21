package org.softwareFm.card.internal.details;

import org.softwareFm.card.editors.internal.TextEditor;

public class TextEditorDetailAdderTest extends AbstractValueEditorDetailAdderTest<TextEditorDetailAdder, TextEditor> {

	@Override
	protected TextEditorDetailAdder makeDetailsAdder() {
		return new TextEditorDetailAdder();
	}

}
