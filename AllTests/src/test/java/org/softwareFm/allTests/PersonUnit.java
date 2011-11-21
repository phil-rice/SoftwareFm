package org.softwareFm.allTests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.utilities.functions.IFunction1;

public class PersonUnit {

	public static void main(String[] args) {
		Show.display("Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
//				Button.makeButtonFromMainMethod(composite, ShowAllSimpleImageButtons.class);
//				Button.makeButtonFromMainMethod(composite, TitleAndText.class);
//				Button.makeButtonFromMainMethod(composite, TitleAndStyledText.class);
//				Button.makeButtonFromMainMethod(composite, TextEditor.class);
//				Button.makeButtonFromMainMethod(composite, JarDetailsLargeButtonFactory.class);
//				Button.makeButtonFromMainMethod(composite, ArtifactDetailsLargeButtonFactory.class);
//				Button.makeButtonFromMainMethod(composite, ArtifactSocialLargeButtonFactory.class);
//				Button.makeButtonFromMainMethod(composite, GroupLargeButtonFactory.class);
//				Button.makeButtonFromMainMethod(composite, SoftwareFmViewUnit.class);
//				Button.makeButtonFromMainMethod(composite, RssPersonUnit.class);
//				Button.makeButtonFromMainMethod(composite, RssBrowserPersonUnit.class);
//				Button.makeButtonFromMainMethod(composite, BrowserUnit.class);
//				Button.makeButtonFromMainMethod(composite, TimeLineUnit.class);
//				Button.makeButtonFromMainMethod(composite, CardUnit.class);
//				Button.makeButtonFromMainMethod(composite, CardCollectionHolder.class);
//				Button.makeButtonFromMainMethod(composite, CardHolder.class);
//				Button.makeButtonFromMainMethod(composite, TextEditor.class);
//				Button.makeButtonFromMainMethod(composite, StyledTextEditor.class);
//				Button.makeButtonFromMainMethod(composite, Explorer.class);
//				Button.makeButtonFromMainMethod(composite, ExplorerUnit.class);
//				Button.makeButtonFromMainMethod(composite, ExplorerWithRadioChannel.class);
				Grid.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				from.pack();
				return composite;
			}
		});
	}
}
