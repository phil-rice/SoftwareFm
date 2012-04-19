/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.navigation;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public class MeteredRepoNavigation implements IRepoNavigation {

	private final Map<String, Integer> urlToCount = Maps.newMap();
	private final IRepoNavigation delegate;

	public MeteredRepoNavigation(IRepoNavigation repoNavigation) {
		delegate = repoNavigation;
	}

	@Override
	public ITransaction<Map<String, List<String>>> navigationData(String url, ICallback<Map<String, List<String>>> callback) {
		Maps.add(urlToCount, url, 1);
		return delegate.navigationData(url, callback);
	}

	public int countFor(String url) {
		return Maps.intFor(urlToCount, url);
	}

}