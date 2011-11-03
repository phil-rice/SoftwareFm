package org.softwareFm.display.constants;

public class DisplayConstants {

	public static final String duplicateEntity = "Duplicate Entity {0}";
	public static final String duplicateUrlGenerator = "Duplicate UrlGenerator {0}";
	public static final String unrecognisedEntity = "Unrecognised entity {0}. Legal values are {1}";
	public static final String mainEntityNotDefined = "Main entity has not been set";
	public static final String unrecognisedUrlGenerator = "Unrecognised UrlGenerator {0}. Legal values are {1}";
	public static final String unrecognisedFeedType = "Unrecognised feed type {0}. Legal values are {1}";
	public static final String mustBeAString = "Must be a string. Value is [{0}]. Class is {1}";
	public static final String illegalPath = "Illegal path [{0}]";
	public static final String cannotFindCachedDataFor = "Cannot find cached data for {0}.{1}";
	public static final String cannotSetValueTwice = "Cannot set value of {0} twice. Current value [{1}]. New value [{2}]";
	public static final String mustHaveSomeLargeButtons = "Must have some large buttons";
	public static final String imageNotFound = "Cannot find image {0}";
	public static final String mustHaveA = "Must have a {0} in {1}";
	public static final String expectedAList = "Expected a List. Value is {0}. Class is {1}. DisplayDefn is {2}";
	public static final String expectedAMap = "Expected a List. Value is {0}. Class is {1}";
	public static final String guardMustHaveEvenParameters = "Expected even number of parameters for guard condition to {0}. Actually had {1}. Which were {2}";
	public static final String cannotParseRssFeed = "Cannot Parse Rss Feed:\n {0}\n";
	public static final String playListMustHaveSomeContent = "Play list {0} must have some content";
	public static final String mustHaveSelectedPlayList = "Time line must have a selected play list";
	public static final String noPreviousItemInPlayList = "No previous item in play list";
	public static final String cannotHaveTwoDefaultActions = "Cannot have two default actions in displayer defn for data {0}.\nExisting value{1}\nNew value{2}";
	public static final String cannotDefineActionsTwice = "Cannot define actions twice in displayer defn for data {0}.\nExisting value{1}\nNew value{2}";
	public static final String cannotSetEditorTwice = "Cannot define editor twice in displayer defn for data {0}.\nExisting value{1}\nNew value{2}";
	public static final String displayDefnMustHaveLegalDataKey = "Display defn must have legal data key {0}";
	public static final String urlCannotBeFoundForEntity = "Url cannot be found for entity {0}";
	public static final String illegalColorString = "Color key: {0} has value {1} which is not a valid color";

	public static final String rssFeedType = "feedType.rss";
	public static final String browserFeedType = "feedType.browser";

	public static final String rssTitleKey = "rss.title";
	public static final String cannotFindValueForKey = "Cannot find value for key {0} in {1}";
	public static final String defaultPlayListName = "defaultPlayListName";
	public static String[] defaultPlayList = new String[] { rssFeedType, "http://www.theregister.co.uk/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/hardware/pc_chips/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/hardware/servers/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/hardware/virtualization/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/hardware/sysadmin_blog/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/security/crime/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/business/finance/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/business/cio/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/cloud/headlines.atom",//
			rssFeedType, "http://www.theregister.co.uk/science/headlines.atom",//
			rssFeedType, "http://feeds.geekzone.co.nz/GeekzoneBlogUsers" };//
	public static final String buttonCancelTitle = "button.cancel.title";
	public static final String buttonOkTitle = "button.ok.title";

}
