package org.arc4eclipse.utilities.resources;

import java.util.ResourceBundle;

public interface IResourceGetter {

	String getStringOrNull(String fullKey);

	IResourceGetter with(IResourceGetter getter);

	IResourceGetter with(Class<?> anchorClass, String propertyName);

	IResourceGetter with(ResourceBundle bundle);

	public static class Utils {
		public static IResourceGetter noResources() {
			return new ResourceGetter(null);
		}

	}
}
