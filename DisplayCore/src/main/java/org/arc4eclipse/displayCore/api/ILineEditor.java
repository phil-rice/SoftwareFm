package org.arc4eclipse.displayCore.api;

public interface ILineEditor {

	public static class Utils {
		public static ILineEditor noLineEditor() {
			return new ILineEditor() {
				@Override
				public String toString() {
					return "noLineEditor";
				}
			};
		}
	}
}
