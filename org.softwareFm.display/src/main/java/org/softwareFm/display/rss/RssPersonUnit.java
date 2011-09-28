package org.softwareFm.display.rss;

import java.io.File;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.swt.ISituationDisplayer;
import org.softwareFm.display.swt.SituationListAnd;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class RssPersonUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/rss");
		Swts.display("Rss Person Unit", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Callable<? extends Iterable<String>> situations = new Callable<Iterable<String>>() {
					@Override
					public Iterable<String> call() throws Exception {
						return Iterables.map(Files.walkChildrenOf(root, Files.extensionFilter("xml")), Files.toFileName());
					}
				};
				IFunction1<Composite, ISituationDisplayer> childWindowCreator = new IFunction1<Composite, ISituationDisplayer>() {
					@Override
					public ISituationDisplayer apply(final Composite from) throws Exception {
						RssDisplay rssDisplay = new RssDisplay(from, SWT.BORDER, root, IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test"));
						return rssDisplay;
					}
				};
				SituationListAnd result = new SituationListAnd(from,  situations, childWindowCreator);
				result.selectFirst();
				return result.getComposite();
			}
		});
	}
}
