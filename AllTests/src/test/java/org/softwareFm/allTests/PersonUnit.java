package org.softwareFm.allTests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwarefm.display.SoftwareFmDisplaySamples;
import org.softwarefm.display.composites.TitleAndText;
import org.softwarefm.display.editor.TextEditor;
import org.softwarefm.display.samples.Sample;
import org.softwarefm.display.samples.ShowAllSimpleImageButtons;

public class PersonUnit {

	public static void main(String[] args) {
		Swts.display("Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				Swts.makeButtonFromMainMethod(composite, ShowAllSimpleImageButtons.class);
				Swts.makeButtonFromMainMethod(composite, TitleAndText.class);
				Swts.makeButtonFromMainMethod(composite, TextEditor.class);
				Swts.makeButtonFromMainMethod(composite, Sample.class);
				Swts.makeButtonFromMainMethod(composite, SoftwareFmDisplaySamples.class);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

		});
	}
}
