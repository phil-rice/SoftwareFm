package org.softwarefm.eclipse.templates;

import java.util.Map;

import org.softwarefm.eclipse.templates.internal.TemplateStore;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.maps.Maps;

public interface ITemplateStore {

	String getTemplate(String name);

	public static class Utils {
		public static ITemplateStore forTests(final String... nameAndValues) {
			return new ITemplateStore() {
				private final Map<String, String> map = Maps.<String, String> makeMap((Object[]) nameAndValues);

				public String getTemplate(String name) {
					return map.get(name);
				}
			};
		}
		
		public static ITemplateStore templateStore(IUrlStrategy urlStrategy){
			return new TemplateStore(urlStrategy);
		}
	}

}
