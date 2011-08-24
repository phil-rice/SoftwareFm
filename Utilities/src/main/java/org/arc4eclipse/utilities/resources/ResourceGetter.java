package org.arc4eclipse.utilities.resources;

import java.util.Locale;
import java.util.ResourceBundle;

class ResourceGetter implements IResourceGetter {

	private final IResourceGetter parent;

	public ResourceGetter(IResourceGetter parent) {
		this.parent = parent;
	}

	@Override
	public String getStringOrNull(String fullKey) {
		String result = localGet(fullKey);
		if (result == null)
			if (parent == null)
				return null;
			else
				return parent.getStringOrNull(fullKey);
		return result;
	}

	protected String localGet(String fullKey) {
		return null;
	}

	@Override
	public IResourceGetter with(final IResourceGetter getter) {
		return new ResourceGetter(this) {
			@Override
			protected String localGet(String fullKey) {
				return getter.getStringOrNull(fullKey);
			}
		};
	}

	@Override
	public IResourceGetter with(final ResourceBundle bundle) {
		return new ResourceGetter(this) {
			@Override
			protected String localGet(String fullKey) {
				if (bundle.containsKey(fullKey))
					return bundle.getString(fullKey);
				else
					return null;
			}
		};
	}

	@Override
	public IResourceGetter with(Class<?> anchorClass, String propertyName) {
		ResourceBundle bundle = ResourceBundle.getBundle(anchorClass.getPackage().getName() + "." + propertyName, Locale.getDefault(), anchorClass.getClassLoader());
		return with(bundle);
	}

}