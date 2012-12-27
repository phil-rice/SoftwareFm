package org.softwarefm.utilities.time;

import java.sql.Date;

public interface ITime {
	
	Date getNow();
	
	public static class Utils{
		public static ITime system(){
			return new ITime() {
				public Date getNow() {
					return new Date(new java.util.Date().getTime());				}
			};
		}

		public static ITime dummy(final java.util.Date date) {
			return new ITime() {
				public Date getNow() {
					return new Date(date.getTime());				}
			};
		}
	}

}
