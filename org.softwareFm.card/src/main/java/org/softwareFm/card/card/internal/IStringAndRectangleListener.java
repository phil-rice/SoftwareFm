package org.softwareFm.card.card.internal;

import org.eclipse.swt.graphics.Rectangle;

public interface IStringAndRectangleListener {

	void drawing(String message, Rectangle rectangle);

	public static class Utils {
		public static IStringAndRectangleListener noListener() {
			return new IStringAndRectangleListener() {
				@Override
				public void drawing(String message, Rectangle rectangle) {
				}
			};
		}

		public static IStringAndRectangleListener sysout() {
			return new IStringAndRectangleListener() {
				@Override
				public void drawing(String message, Rectangle rectangle) {
					System.out.println(message + ": " + rectangle);
				}
			};
		}
	}
}
