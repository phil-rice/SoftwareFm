package org.arc4eclipse.swtBasics.images;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.utilities.resources.IResourceGetter;

public class Resources {

	public static String getAddKey(String key) {
		return MessageFormat.format(SwtBasicConstants.addTooltipPattern, key);
	}

	public static String getRowKey(String key) {
		return MessageFormat.format(SwtBasicConstants.rowPattern, key);
	}

	public static String getNameAndValue_Name(IResourceGetter resourceGetter, String key) {
		return getResource(resourceGetter, SwtBasicConstants.nameAndValue_namePattern, key);
	}

	public static String getNameAndValue_Value(IResourceGetter resourceGetter, String key) {
		return getResource(resourceGetter, SwtBasicConstants.nameAndValue_valuePattern, key);
	}

	public static String getTooltip(IResourceGetter resourceGetter, String key) {
		return getResource(resourceGetter, SwtBasicConstants.tooltipPattern, key);
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
		return getResource(resourceGetter, SwtBasicConstants.titlePattern, key);
	}

	public static String getHelp(IResourceGetter resourceGetter, String key) {
		return getResource(resourceGetter, SwtBasicConstants.summaryHelpPattern, key);
	}

	public static String getDetailedHelp(IResourceGetter resourceGetter, String helpKey) {
		return getResource(resourceGetter, SwtBasicConstants.detailedhelpPattern, helpKey);
	}

	private static String getResource(IResourceGetter resourceGetter, String pattern, String key) {
		String fullKey = MessageFormat.format(pattern, key);
		String result = IResourceGetter.Utils.get(resourceGetter, fullKey);
		return result;
	}

	public static boolean hasHelpText(IResourceGetter resourceGetter, String key) {
		return getHelp(resourceGetter, key) != null;
	}

}