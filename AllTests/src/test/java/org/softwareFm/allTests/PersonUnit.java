package org.softwareFm.allTests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayJavadocAndSource.JavadocPanel;
import org.softwareFm.displayJavadocAndSource.SourcePanel;
import org.softwareFm.softwareFmImages.images.ImageButtonDemo;
import org.softwareFm.softwareFmImages.images.ShowAllImages;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class PersonUnit {

	public static void main(String[] args) {
		Swts.display("Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				Swts.makeButtonFromMainMethod(composite, ShowAllImages.class);
				Swts.makeButtonFromMainMethod(composite, ImageButtonDemo.class);
				Swts.makeButtonFromMainMethod(composite, JavadocPanel.class);
				Swts.makeButtonFromMainMethod(composite, SourcePanel.class);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

		});
	}
}
