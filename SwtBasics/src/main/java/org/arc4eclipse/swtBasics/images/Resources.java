package org.arc4eclipse.swtBasics.images;

import java.text.MessageFormat;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.arc4eclipse.utilities.resources.IResourceGetterBuilder;

public class Resources {

	public static String getTooltip(IResourceGetter getter, String key) {
		return getter.getString(MessageFormat.format(SwtBasicConstants.tooltipPattern, key));
	}

	public static String getMainName(String key) {
		return MessageFormat.format(SwtBasicConstants.mainImagePattern, key);
	}

	public static String getDepressedName(String key) {
		return MessageFormat.format(SwtBasicConstants.depressedImagePattern, key);
	}

	public static IResourceGetterBuilder builderWithBasics() {
		return IResourceGetterBuilder.Utils.getter("Basic");
	}

	public static String getTitle(IResourceGetter resourceGetter, String key) {
		return resourceGetter.getString(MessageFormat.format(SwtBasicConstants.titlePattern, key));
	}

	public static String getHelp(IResourceGetter resourceGetter, String key) {
		return resourceGetter.getString(MessageFormat.format(SwtBasicConstants.helpPattern, key));
	}

	public static String getDetailedHelp(IResourceGetter resourceGetter, String helpKey) {
		return resourceGetter.getString(MessageFormat.format(SwtBasicConstants.detailedhelpPattern, helpKey));
	}

	public static boolean hasHelpText(IResourceGetter resourceGetter, String key) {
		return getHelp(resourceGetter, key) != null;
	}

}
