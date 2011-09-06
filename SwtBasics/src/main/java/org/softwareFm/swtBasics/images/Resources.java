package org.softwareFm.swtBasics.images;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.utilities.resources.IResourceGetter;

public class Resources {

	public static String getAddKey(String key) {
		return MessageFormat.format(SwtBasicConstants.addTooltipPattern, key);
	}

	public static String getRowKey(String key) {
		return MessageFormat.format(SwtBasicConstants.rowPattern, key);
	}

	public static String getNameAndValue_Name(IResourceGetter resourceGetter, String key) {
		return getResourceOrException(resourceGetter, SwtBasicConstants.nameAndValue_namePattern, key);
	}

	public static String getNameAndValue_Value(IResourceGetter resourceGetter, String key) {
		return getResourceOrException(resourceGetter, SwtBasicConstants.nameAndValue_valuePattern, key);
	}

	public static String getTooltip(IResourceGetter resourceGetter, String key) {
		return getResourceOrException(resourceGetter, SwtBasicConstants.tooltipPattern, key);
	}

	public static String getMainName(String key) {
		return MessageFormat.format(SwtBasicConstants.mainImagePattern, key);
	}

	public static String getDepressedName(String key) {
		return MessageFormat.format(SwtBasicConstants.depressedImagePattern, key);
	}

	public static IResourceGetter resourceGetterWithBasics(String... extras) {
		IResourceGetter getter = IResourceGetter.Utils.noResources().with(Images.class, "Basic");
		for (String extra : extras) {
			getter = getter.with(ResourceBundle.getBundle(extra, Locale.getDefault()));
		}
		return getter;
	}

	public static String getTitle(IResourceGetter resourceGetter, String key) {
		return getResourceOrException(resourceGetter, SwtBasicConstants.titlePattern, key);
	}

	public static String getHelp(IResourceGetter resourceGetter, String key) {
		return getResourceOrException(resourceGetter, SwtBasicConstants.summaryHelpPattern, key);
	}

	public static String getDetailedHelp(IResourceGetter resourceGetter, String helpKey) {
		return getResourceOrException(resourceGetter, SwtBasicConstants.detailedhelpPattern, helpKey);
	}

	private static String getResourceOrException(IResourceGetter resourceGetter, String pattern, String key) {
		String fullKey = MessageFormat.format(pattern, key);
		String result = getOrException(resourceGetter, fullKey);
		return result;
	}

	public static boolean hasHelpText(IResourceGetter resourceGetter, String key) {
		return getHelp(resourceGetter, key) != null;
	}

	public static String getOrException(IResourceGetter resourceGetter, String key) {
		String result = IResourceGetter.Utils.get(resourceGetter, key);
		if (result == null)
			throw new RuntimeException(MessageFormat.format(SwtBasicConstants.missingResource, key));
		return result;
	}

}
