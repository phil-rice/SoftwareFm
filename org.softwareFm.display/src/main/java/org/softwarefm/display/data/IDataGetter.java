/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.data;

import java.util.List;
import java.util.Map;

public interface IDataGetter {
	Object getDataFor(String key);

	ActionData getActionDataFor(List<String> params);

	Map<String, Object> getLastRawData(String entity);

	void setRawData(String entity, Map<String, Object> rawData);

	void clearCache(String url, String entity, String attribute);
}