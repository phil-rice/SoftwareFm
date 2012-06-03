/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.resources;

import java.util.Map;
import java.util.ResourceBundle;

import org.softwarefm.utilities.maps.Maps;

public class ResourceGetterMock implements IResourceGetter {

	private final Map<String, String> map;

	public ResourceGetterMock(String... namesAndValues) {
		this.map = Maps.<String, String> makeLinkedMap((Object[]) namesAndValues);
	}

	
	public String getStringOrNull(String fullKey) {
		return map.get(fullKey);
	}

	
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

	
	public IResourceGetter with(Class<?> anchorClass, String propertyName) {
		throw new UnsupportedOperationException();
	}

	
	public IResourceGetter with(ResourceBundle bundle) {
		throw new UnsupportedOperationException();
	}

}