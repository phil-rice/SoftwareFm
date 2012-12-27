package org.softwarefm.core.templates.internal;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwarefm.core.templates.ITemplateStore;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.maps.Maps;

public class TemplateStore implements ITemplateStore {

	private final IUrlStrategy urlStrategy;
	final Map<String, String> cache = Maps.newMap();

	public TemplateStore(IUrlStrategy urlStrategy) {
		this.urlStrategy = urlStrategy;
	}

	public String getTemplate(final String name) {
		return Maps.findOrCreate(cache, name, new Callable<String>() {
			public String call() throws Exception {
				HostOffsetAndUrl hostOffsetAndUrl = urlStrategy.templateUrl(name);
				URL url = hostOffsetAndUrl.getHttpUrl();
				return Files.getText(url.openStream()); // has try/finally
			}
		});
	}
}
