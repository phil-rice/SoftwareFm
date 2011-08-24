package org.arc4eclipse.swtBasics.images;

import java.text.MessageFormat;
import java.util.Arrays;

import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.arc4eclipse.utilities.resources.IResourceGetterBuilder;

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

	public static IResourceGetterBuilder builderWithBasics(String... extras) {
		String[] array = Lists.addAtStart(Arrays.asList(extras), SwtBasicConstants.basicResources).toArray(new String[0]);
		IResourceGetterBuilder result = IResourceGetterBuilder.Utils.getter(array);
		return result;
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
		return resourceGetter.getString(MessageFormat.format(pattern, key));
	}

	public static boolean hasHelpText(IResourceGetter resourceGetter, String key) {
		return getHelp(resourceGetter, key) != null;
	}

}
