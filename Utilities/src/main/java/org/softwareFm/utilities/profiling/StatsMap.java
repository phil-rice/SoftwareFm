/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.profiling;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class StatsMap<K> implements ISimpleMap<K, Stats> {

	static class Utils {
		public static final <K> Callable<StatsMap<K>> newStatsMap() {
			return new Callable<StatsMap<K>>() {
				@Override
				public StatsMap<K> call() throws Exception {
					return new StatsMap<K>();
				}
			};
		}
	}

	private final Map<K, Stats> map = Maps.newMap();

	public void add(K job, long duration) {
		Maps.findOrCreate(map, job, new Callable<Stats>() {
			@Override
			public Stats call() throws Exception {
				return new Stats();
			}
		}).add(duration);
	}

	@Override
	public Stats get(K key) {
		return map.get(key);
	}

	@Override
	public List<K> keys() {
		return Iterables.list(map.keySet());
	}

}