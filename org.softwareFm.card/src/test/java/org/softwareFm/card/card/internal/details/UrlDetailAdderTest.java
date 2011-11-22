package org.softwareFm.card.card.internal.details;

import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.editors.internal.UrlEditor;
import org.softwareFm.card.editors.internal.UrlEditorDetailAdder;

public class UrlDetailAdderTest extends AbstractValueEditorDetailAdderTest<UrlEditorDetailAdder, UrlEditor> {

	@Override
	protected UrlEditorDetailAdder makeDetailsAdder() {
		return (UrlEditorDetailAdder) IEditorDetailAdder.Utils.url();
	}

}
