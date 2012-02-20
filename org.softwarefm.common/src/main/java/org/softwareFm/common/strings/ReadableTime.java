package org.softwareFm.common.strings;

import java.text.MessageFormat;

import org.softwareFm.common.exceptions.WrappedException;

public class ReadableTime {
	public static long millisPerMinute = 1000 * 60;
	public static long millisPerHour = millisPerMinute * 60;
	public static long millisPerDay = millisPerHour * 24;
	public static long millisPerMonth = millisPerDay * 30;// ok so this is approx...
	public static long millisPeryear = millisPerDay * 365;

	public final String future = "in the future!";
	public final String justNow = "just now";
	public final String minutesAgoPattern = "{0} minute(s) ago";
	public final String hourAgoPattern = "{0} hour(s) ago";
	public final String hoursAgoPattern = "{0} hour(s) ago";
	public final String daysAgoPattern = "{0} day(s) ago";
	public final String monthsAgoPattern = "{0} month(s) ago";
	public final String yearsAgoPattern = "{0} year(s) ago";

	public String readableNameFor(long now, long then) {
		return process(now, then, new IReadTimeCallback() {
			@Override
			public String process(String pattern, long units) {
				String raw = MessageFormat.format(pattern, units);
				String result = raw.replace("(s)", units == 1 ? "" : "s");
				return result;
			}
		});
	}

	public static interface IReadTimeCallback {
		String process(String pattern, long units);
	}

	public String process(long now, long then, IReadTimeCallback callback) {
		try {
			if (now < then)
				return callback.process(future, 0l);
			if (now < then + millisPerMinute)
				return callback.process(justNow, 0l);
			if (now < then + millisPerHour)
				return callback.process(minutesAgoPattern, (now - then) / millisPerMinute);
			if (now < then + millisPerDay)
				return callback.process(hoursAgoPattern, (now - then) / millisPerHour);
			if (now < then + millisPerMonth)
				return callback.process(daysAgoPattern, (now - then) / millisPerDay);
			if (now < then + millisPeryear)
				return callback.process(monthsAgoPattern, (now - then) / millisPerMonth);
			return callback.process(yearsAgoPattern, (now - then) / millisPeryear);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
