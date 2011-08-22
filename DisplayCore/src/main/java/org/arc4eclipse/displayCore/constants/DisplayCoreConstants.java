package org.arc4eclipse.displayCore.constants;

import java.util.Arrays;
import java.util.List;

public class DisplayCoreConstants {

	public static final String jarDigestTitle = "Jar Digest";
	public static final String jarPathTitle = "Jar Path";
	public static final String jarNameTitle = "Jar Name";

	public static final List<String> keysToIgnore = Arrays.asList("jcr:primaryType");
	public static final String ripperResult = "ripperResult";
	public static final String illegalEntityName = "Illegal Entity name {0}. Known values are {1}";
	public static final String illegalKey = "Illegal key {0} for entity {1}. Known values are {2}";

	public static final String key = "key";
	public static final String displayer = "displayer";
	public static final String title = "title";
	public static final String help = "help";
	public static final String mustHaveDisplayer = "Must have displayer in map {0}";
	public static final String missingValueInMap = "Must have a value for {0} in map {1}";
	public static final String displayerNotFound = "Illegal value for displayer {0}. Legal values are {1} in map {2}";

}
