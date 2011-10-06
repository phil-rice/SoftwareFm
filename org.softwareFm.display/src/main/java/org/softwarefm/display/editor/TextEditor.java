package org.softwareFm.display.editor;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndText;

public class TextEditor extends AbstractTextEditor<Text> {

	@Override
	protected AbstractTitleAndText<Text> makeTitleAnd(Composite parent, CompositeConfig config) {
		return new TitleAndText(config, parent, "", false);
	}

	public static void main(String[] args) {
		Editors.display("TextEditor", new TextEditor(), Arrays.asList("one", "two"), Arrays.<Object> asList("1", "2"), "Text");
	}

}
