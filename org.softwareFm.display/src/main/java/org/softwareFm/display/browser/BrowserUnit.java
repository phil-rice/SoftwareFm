package org.softwareFm.display.browser;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.services.IServiceExecutor;

public class BrowserUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/browser");
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		try {
			Show.xUnit("Browser Unit", root, "txt", //
					new ISituationListAndBuilder<BrowserComposite>() {

						@Override
						public BrowserComposite makeChild(Composite from) {
							Display display = from.getDisplay();
							IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
							ImageRegistry imageRegistry = new ImageRegistry();
							new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
							CompositeConfig config = new CompositeConfig(display, new SoftwareFmLayout(), imageRegistry, resourceGetter);
							BrowserComposite composite = new BrowserComposite(from, SWT.NULL, service);
							new RssFeedConfigurator().configure(config, composite);
							new BrowserFeedConfigurator().configure(config, composite);
							return composite;
						}

						@Override
						public void selected(BrowserComposite hasControl, String fileName, Object json) throws Exception {
							Map<String, Object> map = Json.mapFromString(json.toString());

							String feedType = (String) Maps.getOrException(map, "feedType");
							String url = (String) Maps.getOrException(map, "url");
							hasControl.processUrl(feedType, url);
						}
					});
		} finally {
			service.shutdown();
		}
	}
}
