package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.utilities.strings.NameAndValue;
import org.softwareFm.utilities.strings.Strings;

public class Actions {
	public static String getDataKey(DisplayerDefn displayerDefn, List<String> formalParams) {
		if (formalParams == null|| formalParams.size() == 0)
			return displayerDefn.dataKey;
		else
			return formalParams.get(0);
	}

	public static String guardConditionPresent(IDataGetter dataGetter, DisplayerDefn displayerDefn) {
		return guardConditionPresent(dataGetter, displayerDefn, null);

	}

	public static String guardConditionPresent(IDataGetter dataGetter, DisplayerDefn displayerDefn, String idToIgnore) {
		if (displayerDefn.guardKeys != null)
			for (NameAndValue guard : displayerDefn.guardKeys) {
				if (idToIgnore!= null && idToIgnore.equals(guard.name))
					return null;
				String guardValue = Strings.nullSafeToString(dataGetter.getDataFor(guard.name));
				if (guardValue == null || guardValue.length() == 0)
					return guard.value;
			}
		return null;
	}

	public static Object getValueFor(IDataGetter dataGetter, DisplayerDefn displayerDefn) {
		String guard = guardConditionPresent(dataGetter, displayerDefn);
		if (guard == null)
			return dataGetter.getDataFor(displayerDefn.dataKey);
		else
			return dataGetter.getDataFor(guard);
	}
}
