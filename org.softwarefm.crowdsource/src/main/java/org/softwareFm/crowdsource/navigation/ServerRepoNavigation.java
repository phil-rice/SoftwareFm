/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.navigation;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class ServerRepoNavigation implements IRepoNavigation {

	private final File root;

	public ServerRepoNavigation(File root) {
		super();
		this.root = root;
	}

	@Override
	public ITransaction<Map<String, List<String>>> navigationData(String url, ICallback<Map<String, List<String>>> callback) {
		List<String> urlsToRoot = Urls.urlsToRoot(url);
		Map<String, List<String>> result = Maps.newMap(LinkedHashMap.class);
		for (String pathUrl : urlsToRoot)
			result.put(pathUrl, findChildrenOf(pathUrl));
		ICallback.Utils.call(callback, result);
		return ITransaction.Utils.doneTransaction(result);
	}

	private List<String> findChildrenOf(String pathUrl) {
		List<String> result = Lists.map(Arrays.asList(Files.listChildDirectoriesIgnoringDot(new File(root, pathUrl))), Files.fileNameFn);
		return result;
	}

}