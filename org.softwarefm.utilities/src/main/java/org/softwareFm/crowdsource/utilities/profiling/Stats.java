/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.profiling;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class Stats {

	StandardDeviation deviation = new StandardDeviation();
	private long totalDuration;
	int count;

	public void add(long duration) {
		count++;
		totalDuration += duration;
		deviation.increment(duration);
	}

	public long averageDuration() {
		return count == 0 ? 0 : totalDuration / count;
	}

	public double standardDeviation() {
		return deviation.getResult();
	}

	public String display(String format, double scale) {
		return "[Stats: " + String.format("Avg: " + format + " Count: %d", averageDuration() / scale, count) + "]";

	}

	@Override
	public String toString() {
		return "[Stats: " + String.format("Avg: %d Count: %d", averageDuration(), count) + "]";
	}

}