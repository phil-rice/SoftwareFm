package org.softwareFm.utilities.resources;

import java.util.Map;
import java.util.ResourceBundle;

import org.softwareFm.utilities.maps.Maps;

public class ResourceGetterMock implements IResourceGetter {

	private final Map<String, String> map;

	public ResourceGetterMock(String... namesAndValues) {
		this.map = Maps.<String, String> makeLinkedMap((Object[]) namesAndValues);
	}

	@Override
	public String getStringOrNull(String fullKey) {
		return map.get(fullKey);
	}

	@Override
	public IResourceGetter with(final IResourceGetter getter) {
		return new ResourceGetterMock() {
			@Override
			public String getStringOrNull(String fullKey) {
				String first = ResourceGetterMock.this.getStringOrNull(fullKey);
				if (first != null)
					return first;
				return getter.getStringOrNull(fullKey);
			}
		};

	}

	@Override
	public IResourceGetter with(Class<?> anchorClass, String propertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IResourceGetter with(ResourceBundle bundle) {
		throw new UnsupportedOperationException();
	}

}