package org.softwareFm.utilities.profiling;

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
		return totalDuration / count;
	}

	public double standardDeviation() {
		return deviation.getResult();
	}

}