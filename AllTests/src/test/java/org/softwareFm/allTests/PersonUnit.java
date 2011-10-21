package org.softwareFm.allTests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.card.internal.CardHolder;
import org.softwareFm.card.internal.CardUnit;
import org.softwareFm.card.internal.SingleCardExplorer;
import org.softwareFm.card.internal.SingleCardExplorerOnAsync;
import org.softwareFm.card.internal.SingleCardExplorerUnit;
import org.softwareFm.configuration.largebuttons.ArtifactDetailsLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.ArtifactSocialLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.GroupLargeButtonFactory;
import org.softwareFm.configuration.largebuttons.JarDetailsLargeButtonFactory;
import org.softwareFm.display.browser.BrowserUnit;
import org.softwareFm.display.composites.TitleAndStyledText;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.editor.TextEditor;
import org.softwareFm.display.rss.RssBrowserPersonUnit;
import org.softwareFm.display.rss.RssPersonUnit;
import org.softwareFm.display.samples.ShowAllSimpleImageButtons;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.timeline.TimeLineUnit;
import org.softwareFm.eclipse.sample.SoftwareFmViewUnit;
import org.softwareFm.utilities.functions.IFunction1;

public class PersonUnit {

	public static void main(String[] args) {
		Swts.display("Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				Swts.makeButtonFromMainMethod(composite, ShowAllSimpleImageButtons.class);
				Swts.makeButtonFromMainMethod(composite, TitleAndText.class);
				Swts.makeButtonFromMainMethod(composite, TitleAndStyledText.class);
				Swts.makeButtonFromMainMethod(composite, TextEditor.class);
				Swts.makeButtonFromMainMethod(composite, JarDetailsLargeButtonFactory.class);
				Swts.makeButtonFromMainMethod(composite, ArtifactDetailsLargeButtonFactory.class);
				Swts.makeButtonFromMainMethod(composite, ArtifactSocialLargeButtonFactory.class);
				Swts.makeButtonFromMainMethod(composite, GroupLargeButtonFactory.class);
				Swts.makeButtonFromMainMethod(composite, SoftwareFmViewUnit.class);
				Swts.makeButtonFromMainMethod(composite, RssPersonUnit.class);
				Swts.makeButtonFromMainMethod(composite, RssBrowserPersonUnit.class);
				Swts.makeButtonFromMainMethod(composite, BrowserUnit.class);
				Swts.makeButtonFromMainMethod(composite, TimeLineUnit.class);
				Swts.makeButtonFromMainMethod(composite, CardUnit.class);
				Swts.makeButtonFromMainMethod(composite, SingleCardExplorer.class);
				Swts.makeButtonFromMainMethod(composite, SingleCardExplorerUnit.class);
				Swts.makeButtonFromMainMethod(composite, SingleCardExplorerOnAsync.class);
				Swts.makeButtonFromMainMethod(composite, CardCollectionHolder.class);
				Swts.makeButtonFromMainMethod(composite, CardHolder.class);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				from.pack();
				return composite;
			}
		});
	}
}
