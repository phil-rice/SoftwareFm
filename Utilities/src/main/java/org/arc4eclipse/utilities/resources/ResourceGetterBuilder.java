package org.arc4eclipse.utilities.resources;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.constants.UtilityMessages;

public class ResourceGetterBuilder implements IResourceGetterBuilder {

	private final List<ResourceBundle> bundles = Lists.newList();

	@Override
	public IResourceGetterBuilder register(ResourceBundle resourceBundle) {
		bundles.add(resourceBundle);
		return this;
	}

	@Override
	public IResourceGetter build() {
		return new IResourceGetter() {

			@Override
			public String getString(String fullKey) {
				for (ResourceBundle bundle : bundles) {
					if (bundle.containsKey(fullKey))
						return bundle.getString(fullKey);
				}
				throw new IllegalArgumentException(MessageFormat.format(UtilityMessages.missingResource, fullKey));
			}
		};
	}
}
