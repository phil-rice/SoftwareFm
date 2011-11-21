package org.softwareFm.utilities.resources;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class NestedResourceGetterFn implements IFunction1<String, IResourceGetter> {
	private final IResourceGetter baseResourceGetter;
	private final Map<String, IResourceGetter> cache = Maps.newMap();
	private final Class<?> anchorClass;

	public NestedResourceGetterFn(IResourceGetter baseResourceGetter, Class<?> anchorClass) {
		this.baseResourceGetter = baseResourceGetter;
		this.anchorClass = anchorClass;
	}

	@Override
	public IResourceGetter apply(final String from) throws Exception {
		return Maps.findOrCreate(cache, from, new Callable<IResourceGetter>() {
			@Override
			public IResourceGetter call() throws Exception {
				try {
					return from == null ? baseResourceGetter : baseResourceGetter.with(anchorClass, from);
				} catch (Exception e) {
					e.printStackTrace();
					return baseResourceGetter;
				}
			}
		});
	}
}