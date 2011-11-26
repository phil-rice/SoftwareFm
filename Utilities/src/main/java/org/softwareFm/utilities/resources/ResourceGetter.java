/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.resources;

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
				try {
					if (bundle.containsKey(fullKey))
						return bundle.getString(fullKey);
					else
						return null;
				} catch (Exception e) {
					throw new RuntimeException("Key: " + fullKey, e);
				}
			}
		};
	}

	@Override
	public IResourceGetter with(Class<?> anchorClass, String propertyName) {
		ResourceBundle bundle = ResourceBundle.getBundle(anchorClass.getPackage().getName() + "." + propertyName, Locale.getDefault(), anchorClass.getClassLoader());
		return with(bundle);
	}

}