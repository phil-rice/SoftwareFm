package org.arc4eclipse.displayLists;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.arc4eclipse.displayCore.api.ILineEditable;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.widgets.Composite;

public class NameAndUrlLineEditor extends NameAndValueLineEditor {

	public NameAndUrlLineEditor() {
		super(DisplayListsConstants.urlNameKey, DisplayListsConstants.urlValueKey);
	}

	@Override
	protected void addButtons(ILineEditable<NameAndValue> lineEditable, Composite parent, int index, final TitleAndTextField text) {
		ImageButtons.addBrowseButton(lineEditable, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return text.getText();
			}
		});
		super.addButtons(lineEditable, parent, index, text);
	}

	public static void main(String[] args) {
		ListPanel.show(new NameAndUrlLineEditor(), Arrays.asList("name1$value1", "name2$value2"));
	}
}
