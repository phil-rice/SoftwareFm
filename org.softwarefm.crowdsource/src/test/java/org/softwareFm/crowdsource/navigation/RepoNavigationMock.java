package org.softwareFm.crowdsource.navigation;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.url.Urls;

/** Designed to work with the CardDataStoreFixture */
public class RepoNavigationMock implements IRepoNavigation {

	private final Map<String, Map<String, Object>> data;

	public RepoNavigationMock(Object... data) {
		Map<String,Map<String,Object>> rawMap = Maps.makeMap(data);
		this.data = Maps.mapTheMap(rawMap, new IFunction1<String,String>() {
			@Override
			public String apply(String from) throws Exception {
				return Urls.compose(from);//gets rid of any pre and post slashes
			}
		}, Functions.<Map<String,Object>, Map<String,Object>>identity());
	}

	@SuppressWarnings({ "unused" })
	@Override
	public ITransaction<Map<String, List<String>>> navigationData(String url, ICallback<Map<String, List<String>>> callback) {
		Map<String, List<String>> result = Maps.newMap();
		for (String thisUrl : Urls.urlsToRoot(url)) {
			List<String> list = Lists.newList();
			Map<String, Object> map = data.get(thisUrl);
			if (map != null)
				for (Map.Entry<String, Object> e : map.entrySet()) {
					Object value = e.getValue();
					if (e.getValue() instanceof Map<?, ?>)
						list.add(e.getKey());
				}
			result.put(thisUrl, list);
		}
		ICallback.Utils.call(callback, result);
		return ITransaction.Utils.<Map<String, List<String>>> doneTransaction(result);
	}

}
