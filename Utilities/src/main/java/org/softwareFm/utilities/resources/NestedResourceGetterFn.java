/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
					return baseResourceGetter;
				}
			}
		});
	}
}