package org.softwareFm.display.editor;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndStyledText;

public class StyledTextEditor extends AbstractTextEditor<StyledText> {

	@Override
	protected AbstractTitleAndText<StyledText> makeTitleAnd(Composite parent, CompositeConfig config) {
		return new TitleAndStyledText(config, parent, "", false);
	}

	public static void main(String[] args) {
		Editors.display("TextEditor", new StyledTextEditor(), "Text");
	}

}
