package org.softwareFm.softwareFmImages.backdrop;

public class BackdropAnchor {
	public static String prefix = "backdrop.";
	public static String main = prefix + "main";
	public static String depressed = prefix + "depressed";
	public static String group(int size, boolean selected) {
		return prefix + "group" + size + (selected?"Active":"" );
	}

}
