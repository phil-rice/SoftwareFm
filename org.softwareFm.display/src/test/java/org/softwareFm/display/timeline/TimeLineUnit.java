package org.softwareFm.display.timeline;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.browser.BrowserComposite;
import org.softwareFm.display.browser.BrowserFeedConfigurator;
import org.softwareFm.display.browser.IBrowserComposite;
import org.softwareFm.display.browser.RssFeedConfigurator;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class TimeLineUnit {

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/timeline");
		final Map<String, IPlayList> playListGetterSource = Maps.newMap();
		final IPlayListGetter playListGetter = new IPlayListGetter() {
			@Override
			public Future<IPlayList> getPlayListFor(String playListName, ICallback<IPlayList> iCallback) {
				try {
					IPlayList playList = Maps.getOrException(playListGetterSource, playListName);
					iCallback.process(playList);
					return Futures.doneFuture(playList);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		};
		Swts.xUnit("Timeline Unit", root, "txt", //
				new ISituationListAndBuilder<BrowserPlusNextPrevButtons>() {

					@Override
					public BrowserPlusNextPrevButtons makeChild(Composite from) throws Exception {
						Display display = from.getDisplay();
						IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
						ImageRegistry imageRegistry = new ImageRegistry();
						new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
						final CompositeConfig config = new CompositeConfig(display, new SoftwareFmLayout(), imageRegistry, resourceGetter);
						return new BrowserPlusNextPrevButtons(from, SWT.NULL, config, new IFunction1<Composite, IBrowserComposite>() {
							@Override
							public IBrowserComposite apply(Composite from) throws Exception {
								BrowserComposite composite = new BrowserComposite(from, SWT.NULL);
								new RssFeedConfigurator().configure(config, composite);
								new BrowserFeedConfigurator().configure(config, composite);
								return composite;
							}
						}, playListGetter);
					}

					@Override
					public void selected(BrowserPlusNextPrevButtons hasControl, String fileName, String json) throws Exception {
						playListGetterSource.clear();
						Map<String, Object> map = Json.mapFromString(json);
						for (Entry<String, Object> entry : map.entrySet()) {
							String playListName = entry.getKey();
							List<String> items = (List<String>) entry.getValue();
							IPlayList playList = IPlayList.Utils.make(playListName, items.toArray(new String[0]));
							playListGetterSource.put(playListName, playList);
							hasControl.addPlayLists(playListGetterSource.keySet());
						}
					}
				});
	}
}
