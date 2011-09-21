package org.softwareFm.allTests;

import java.awt.image.SampleModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.Swts;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.editor.TextEditor;
import org.softwareFm.display.samples.ShowAllSimpleImageButtons;
import org.softwareFm.eclipse.sample.SoftwareFmDisplaySamples;
import org.softwareFm.utilities.functions.IFunction1;

public class PersonUnit {

	public static void main(String[] args) {
		Swts.display("Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				Swts.makeButtonFromMainMethod(composite, ShowAllSimpleImageButtons.class);
				Swts.makeButtonFromMainMethod(composite, TitleAndText.class);
				Swts.makeButtonFromMainMethod(composite, TextEditor.class);
				Swts.makeButtonFromMainMethod(composite, SampleModel.class);
				Swts.makeButtonFromMainMethod(composite, SoftwareFmDisplaySamples.class);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

		});
	}
}
