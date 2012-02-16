/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.url;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.maps.ISimpleMap;
import org.softwareFm.common.maps.Maps;

public interface IUrlGeneratorMap extends ISimpleMap<String, IUrlGenerator> {

	public static class Utils {
		public static IUrlGeneratorMap urlGeneratorMap(Object... nameAndGenerators) {
			return urlGeneratorMap(Maps.<String, IUrlGenerator> makeMap(nameAndGenerators));
		}

		public static IUrlGeneratorMap urlGeneratorMap(final Map<String, IUrlGenerator> map) {
			return new IUrlGeneratorMap() {

				private List<String> keys;

				@Override
				public IUrlGenerator get(String key) {
					return map.get(key);
				}

				@Override
				public List<String> keys() {
					if (keys == null)
						keys = new ArrayList<String>(map.keySet());
					return keys;
				}

				@Override
				public String toString() {
					return map.toString();
				}

			};
		}
	}
}