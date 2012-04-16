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
