package org.softwareFm.allTests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.internal.CardCollectionHolder;
import org.softwareFm.card.card.internal.CardHolder;
import org.softwareFm.card.card.internal.CardUnit;
import org.softwareFm.configuration.largebuttons.ArtifactDetailsLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.ArtifactSocialLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.GroupLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.JarDetailsLargeButtonFactory;
import org.softwareFm.display.browser.BrowserUnit;
import org.softwareFm.display.composites.TitleAndStyledText;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.editor.StyledTextEditor;
import org.softwareFm.display.editor.TextEditor;
import org.softwareFm.display.rss.RssBrowserPersonUnit;
import org.softwareFm.display.rss.RssPersonUnit;
import org.softwareFm.display.samples.ShowAllSimpleImageButtons;
import org.softwareFm.display.swt.Swts.Button;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.timeline.TimeLineUnit;
import org.softwareFm.eclipse.sample.SoftwareFmViewUnit;
import org.softwareFm.explorer.eclipse.Explorer;
import org.softwareFm.explorer.eclipse.ExplorerUnit;
import org.softwareFm.explorer.eclipse.ExplorerWithRadioChannel;
import org.softwareFm.utilities.functions.IFunction1;

public class PersonUnit {

	public static void main(String[] args) {
		Show.display("Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				Button.makeButtonFromMainMethod(composite, ShowAllSimpleImageButtons.class);
				Button.makeButtonFromMainMethod(composite, TitleAndText.class);
				Button.makeButtonFromMainMethod(composite, TitleAndStyledText.class);
				Button.makeButtonFromMainMethod(composite, TextEditor.class);
				Button.makeButtonFromMainMethod(composite, JarDetailsLargeButtonFactory.class);
				Button.makeButtonFromMainMethod(composite, ArtifactDetailsLargeButtonFactory.class);
				Button.makeButtonFromMainMethod(composite, ArtifactSocialLargeButtonFactory.class);
				Button.makeButtonFromMainMethod(composite, GroupLargeButtonFactory.class);
				Button.makeButtonFromMainMethod(composite, SoftwareFmViewUnit.class);
				Button.makeButtonFromMainMethod(composite, RssPersonUnit.class);
				Button.makeButtonFromMainMethod(composite, RssBrowserPersonUnit.class);
				Button.makeButtonFromMainMethod(composite, BrowserUnit.class);
				Button.makeButtonFromMainMethod(composite, TimeLineUnit.class);
				Button.makeButtonFromMainMethod(composite, CardUnit.class);
				Button.makeButtonFromMainMethod(composite, CardCollectionHolder.class);
				Button.makeButtonFromMainMethod(composite, CardHolder.class);
				Button.makeButtonFromMainMethod(composite, TextEditor.class);
				Button.makeButtonFromMainMethod(composite, StyledTextEditor.class);
				Button.makeButtonFromMainMethod(composite, Explorer.class);
				Button.makeButtonFromMainMethod(composite, ExplorerUnit.class);
				Button.makeButtonFromMainMethod(composite, ExplorerWithRadioChannel.class);
				Grid.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				from.pack();
				return composite;
			}
		});
	}
}
