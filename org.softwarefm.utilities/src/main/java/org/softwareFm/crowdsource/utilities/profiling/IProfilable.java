/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.profiling;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.UtilityMessages;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public interface IProfilable<Situation, Context> {

	Context start(Situation situation) throws Exception;

	void job(Situation situation, Context context) throws Exception;

	void finish(Situation situation, Context context) throws Exception;

	static class Utils {
		public static <K1, Situation, Context> void time(K1 key, Situation key2, Map<K1, StatsMap<Situation>> statsMap, IProfilable<Situation, Context> profilable) throws Exception {
			try {
				Maps.findOrCreate(statsMap, key, StatsMap.Utils.<Situation> newStatsMap());
				Context context = profilable.start(key2);
				long startTime = System.currentTimeMillis();
				profilable.job(key2, context);
				long duration = System.currentTimeMillis() - startTime;
				profilable.finish(key2, context);
				statsMap.get(key).add(key2, duration);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static <K1, Situation> void dump(String firstTitle, String secondTitle, Map<K1, StatsMap<Situation>> statsMap, IFunction1<? super Situation, String> sitToString) {
			List<String> titles = Arrays.asList(firstTitle, secondTitle, UtilityMessages.duration, UtilityMessages.standardDeviation);
			List<Integer> lengths = Lists.map(titles, Strings.length());
			System.out.println(Strings.join(titles, " "));
			List<K1> sortedKey1s = Lists.sort((Collection) statsMap.keySet());
			for (K1 key1 : sortedKey1s) {
				StatsMap<Situation> mapOfStats = statsMap.get(key1);
				List<Situation> sortedKey2s = Lists.sort((List) mapOfStats.keys());
				for (Situation key2 : sortedKey2s) {
					System.out.print(Strings.asData(key1, lengths.get(0)));
					System.out.print(' ');
					System.out.print(Strings.asData(key2, lengths.get(1)));
					System.out.print(' ');
					Stats stats = mapOfStats.get(key2);
					System.out.print(Strings.asData(stats.averageDuration(), lengths.get(2)));
					System.out.print(' ');
					System.out.print(Strings.asData(stats.standardDeviation(), lengths.get(3)));
					System.out.println();
				}
			}

		}
	}
}