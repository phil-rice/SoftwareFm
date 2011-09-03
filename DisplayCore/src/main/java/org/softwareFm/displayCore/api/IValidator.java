package org.softwareFm.displayCore.api;

public interface IValidator {

	public static class Utils {
		public static IValidator noValidator() {
			return new IValidator() {
				@Override
				public String toString() {
					return "noValidator";
				}
			};
		}
	}
}
