package org.arc4eclipse.displayCore.api;

public interface IEditor {

	public static class Utils {
		public static IEditor noEditor() {
			return new IEditor() {
				@Override
				public String toString() {
					return "noEditor";
				}
			};
		}
	}

}
