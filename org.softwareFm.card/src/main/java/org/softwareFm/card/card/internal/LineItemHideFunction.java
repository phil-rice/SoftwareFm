package org.softwareFm.card.card.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class LineItemHideFunction implements ILineItemFunction<Boolean> {
	private final IFunction1<String, IResourceGetter> resourceGetterFn;
	private final String keyshidekey;
	private final Map<String, Set<String>> cache = Maps.newMap();

	public LineItemHideFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String keyshidekey) {
		this.resourceGetterFn = resourceGetterFn;
		this.keyshidekey = keyshidekey;
	}

	@Override
	public Boolean apply(CardConfig cardConfig,  final LineItem from) {
		Set<String> keysToHide = Maps.findOrCreate(cache, from.cardType, new Callable<Set<String>>() {
			@Override
			public Set<String> call() throws Exception {
				String keysToHide = IResourceGetter.Utils.getOr(resourceGetterFn, from.cardType, keyshidekey, "");
				return new HashSet<String>(Strings.splitIgnoreBlanks(keysToHide, ","));
			}
		});
		return keysToHide.contains(from.key);
	}
}