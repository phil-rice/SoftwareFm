package org.softwareFm.display.browser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.rss.ISituationListCallback;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class BrowserUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/browser");
		Swts.xUnit("Browser Unit", root, "txt", //
				new IFunction1<Composite, BrowserComposite>() {
					@Override
					public BrowserComposite apply(final Composite from) throws Exception {
						Display display = from.getDisplay();
						IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
						ImageRegistry imageRegistry = new ImageRegistry();
						new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
						CompositeConfig config = new CompositeConfig(display, new SoftwareFmLayout(), imageRegistry, resourceGetter);

						BrowserComposite composite = new BrowserComposite(from, SWT.NULL);
						new RssFeedConfigurator().configure(config, composite);
						new BrowserFeedConfigurator().configure(config, composite);
						return composite;
					}
				}, new ISituationListCallback<BrowserComposite>() {
					@Override
					public void selected(BrowserComposite hasControl, String value) throws FileNotFoundException {
						String json = Files.getText(new FileReader(new File(root, value)));
						Map<String, Object> map = Json.mapFromString(json);

						String feedType = (String) Maps.getOrException(map, "feedType");
						String url = (String) Maps.getOrException(map, "url");
						hasControl.processUrl(feedType, url);
					}
				});
	}
}
