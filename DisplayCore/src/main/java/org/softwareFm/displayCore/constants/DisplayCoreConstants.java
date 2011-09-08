package org.softwareFm.displayCore.constants;

import java.util.Arrays;
import java.util.List;

public class DisplayCoreConstants {

	public static final List<String> keysToIgnore = Arrays.asList("jcr:primaryType");
	public static final String ripperResult = "ripperResult";

	public static final String illegalEntityName = "Illegal Entity name {0}. Known values are {1}";
	public static final String illegalKey = "Illegal editKey {0} for entity {1}. Known values are {2}";
	public static final String attributeMissing = "Attribute missing critical value. Key [{0}] Class[{1}]";
	public static final String nameMissing = "Bundle missing a name. Anchor class is {0}";

	public static final String key = "key";
	public static final String clazz = "class";
	public static final String name = "name";

	public static final Object smallImageKey = "smallImage";
	public static final String displayer = "displayer";
	public static final String title = "title";
	public static final String help = "help";
	public static final String editor = "editor";
	public static final Object lineEditorKey = "lineEditor";

	public static final String mustHaveKey = "Must have a editKey in map {0}";
	public static final String mustHaveDisplayer = "Must have displayer in map {0}";
	public static final String missingValueInMap = "Must have a value for {0} in map {1}";
	public static final String displayerNotFound = "Illegal value for displayer [{0}]. Legal values are {1} in map {2}";
	public static final String smallImageKeyMissing = "Cannot find small image editKey for {0} in {1}";
	public static final String exceptionInProcess = "Unexpected exception processing {0} {1} {2}";

}
