package org.softwareFm.allTests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayJavadocAndSource.JarSummaryImageButtonDemo;
import org.softwareFm.displayJavadocAndSource.JavadocDisplayer;
import org.softwareFm.displayJavadocAndSource.JavadocPanel;
import org.softwareFm.displayJavadocAndSource.SourceDisplayer;
import org.softwareFm.displayJavadocAndSource.SourcePanel;
import org.softwareFm.displayText.TextDisplayer;
import org.softwareFm.displayUrl.UrlDisplayer;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.images.ImageButtonDemo;
import org.softwareFm.softwareFmImages.images.ShowAllImages;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.text.TitleAndStyledTextField;
import org.softwareFm.swtBasics.text.TitleAndTextField;
import org.softwareFm.utilities.functions.IFunction1;

public class PersonUnit {

	public static void main(String[] args) {
		Swts.display("Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				new Button(composite, SWT.NULL).setImage(Images.makeImage(from.getDisplay(), ArtifactsAnchor.class, "jar.png"));
				Swts.makeButtonFromMainMethod(composite, ShowAllImages.class);
				Swts.makeButtonFromMainMethod(composite, ImageButtonDemo.class);
				Swts.makeButtonFromMainMethod(composite, JarSummaryImageButtonDemo.class);
				Swts.makeButtonFromMainMethod(composite, TitleAndTextField.class);
				Swts.makeButtonFromMainMethod(composite, TitleAndStyledTextField.class);
				Swts.makeButtonFromMainMethod(composite, TextDisplayer.class);
				Swts.makeButtonFromMainMethod(composite, UrlDisplayer.class);
				Swts.makeButtonFromMainMethod(composite, JavadocPanel.class);
				Swts.makeButtonFromMainMethod(composite, SourcePanel.class);
				Swts.makeButtonFromMainMethod(composite, JavadocDisplayer.class);
				Swts.makeButtonFromMainMethod(composite, SourceDisplayer.class);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

		});
	}
}
