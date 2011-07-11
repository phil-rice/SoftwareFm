package org.arc4eclipse.utilities.profiling;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arc4eclipse.utilities.collections.Iterables;
import org.arc4eclipse.utilities.maps.ISimpleMap;
import org.arc4eclipse.utilities.maps.Maps;

public class StatsMap<K> implements ISimpleMap<K, Stats> {

	static class Utils {
		public static final <K> Callable<StatsMap<K>> newStatsMap() {
			return new Callable<StatsMap<K>>() {
				public StatsMap<K> call() throws Exception {
					return new StatsMap<K>();
				}
			};
		}
	}

	private final Map<K, Stats> map = Maps.newMap();

	public void add(K job, long duration) {
		Maps.findOrCreate(map, job, new Callable<Stats>() {
			public Stats call() throws Exception {
				return new Stats();
			}
		}).add(duration);
	}

	public Stats get(K key) {
		return map.get(key);
	}

	public List<K> keys() {
		return Iterables.list(map.keySet());
	}

}
