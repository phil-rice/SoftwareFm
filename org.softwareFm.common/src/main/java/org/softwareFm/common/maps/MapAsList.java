/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.maps;

import java.util.Arrays;
import java.util.List;

public class MapAsList {

	public List<String> titles;
	public List<List<Object>> values;
	public final int keyIndex;

	public MapAsList(List<String> titles, int keyIndex, List<Object>... values) {
		this(titles, keyIndex, Arrays.asList(values));
	}

	public MapAsList(List<String> titles, int keyIndex, List<List<Object>> values) {
		this.titles = titles;
		this.keyIndex = keyIndex;
		this.values = values;
	}

	@Override
	public String toString() {
		return "MapAsList [titles=" + titles + ", values=" + values + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((titles == null) ? 0 : titles.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapAsList other = (MapAsList) obj;
		if (titles == null) {
			if (other.titles != null)
				return false;
		} else if (!titles.equals(other.titles))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

}