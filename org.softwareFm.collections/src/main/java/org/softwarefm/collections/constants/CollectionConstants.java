/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.constants;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.utilities.strings.Urls;

public class CollectionConstants {

	public static final String artifactId = "artifactId";
	public static final String groupId = "groupId";
	public static final String jarStem = "jarStem";
	public static final String version = "version";
	public static final String rootUrl = "/softwareFm";
	public static final String rootDataUrl = Urls.composeWithSlash(rootUrl, "data");
	public static final String rootSnippetUrl = Urls.composeWithSlash(rootUrl, "snippet");
	public static final String rootJarUrl = Urls.composeWithSlash(rootUrl, "jar");
	public static final List<String> rootUrlList = Arrays.asList(rootDataUrl, rootJarUrl, rootSnippetUrl);
	public static final String jarNotRecognisedTitle = "jar.notRecognised.title";
	public static final String jarNotRecognisedText = "jar.notRecognised.text";
	
	public static final String jarRtNotRecognisedTitle = "jar.rt.notRecognised.title";
	public static final String jarRtNotRecognisedText = "jar.rt.notRecognised.text";

	public static final String jarImportingTitle = "jar.importing.title";
	public static final String jarImportingText = "jar.importing.text";

	public static final String jarNotRecognisedCardType = "unrecognised";

	public static final String fileNotAJarCardType = "notAJar";
	public static final String fileNotAJarTitle = "file.notAJar.title";
	public static final String fileNotAJarText = "file.notAJar.text";

	public static final String comment = "comment";
	public static final String commentsTitle = "comment.title";
	public static final String commentsNoTitle = "comment.no.title";

	public static final String addCommentButtonTitle = "button.comment.add.title";
	public static final String addCommentButtonImage = "button.comment.add.image";

	public static final String commentsTitleKey = "title";
	public static final String commentsTextKey = "text";
	public static final String createdTimeKey = "created";
	public static final String commentTitleKey = "title";
	public static final String commentTextKey = "text";

	public static final String actionWelcomeTitle = "action.welcome.title";
	public static final String actionRefreshTitle = "action.refresh.title";
	public static final String actionGroupTitle = "action.group.title";
	public static final String actionArtifactTitle = "action.artifact.title";
	public static final String actionVersionTitle = "action.version.title";
	public static final String actionJarTitle = "action.jar.title";
	public static final String actionDebugTitle = "action.debug.title";
	public static final String actionNukeTitle = "action.nuke.title";
	public static final String actionSnippetTitle = "action.snippet.title";

	public static final String actionWelcomeImage = "welcome.gif";
	public static final String actionGroupImage = "group.gif";
	public static final String actionRefreshImage = "refresh.gif";
	public static final String actionArtifactImage = "artifact.gif";
	public static final String actionVersionImage = "version.gif";
	public static final String actionJarImage = "jar.gif";
	public static final String actionDebugImage = "debug.gif";
	public static final String actionNukeImage = "nuke.gif";
	public static final String actionSnippetImage = "snippet.gif";
	
	public static final String jarNotRecognisedSearchingText = "jar.notRecognised.searching.text" ;
	public static final String jarNotRecognisedFound0Text = "jar.notRecognised.found0.text" ;
	public static final String jarNotRecognisedFound1Text = "jar.notRecognised.found1.text" ;
	public static final String jarNotRecognisedFoundManyText = "jar.notRecognised.foundMany.text" ;
	public static final String jarSearchButtonTitle = "button.jar.search";
	public static final String jarSearchWithMavenButtonTitle = "button.jarAndMaven.search";
	public static final String helpUnrecognisedPleaseAddText = "help.unrecognised.pleaseAdd.text";
	public static final String helpUnrecognisedThankYouText = "help.unrecognised.thankYou.text";
	public static final String eclipseProject = "unrecognised.eclipseProject.text";
	public static final String unrecognisedJarName = "unrecognised.jarName.text";
	public static final String unreocgnisedJarPath = "unrecognised.jarPath.text";
	public static final String confirmDelete = "Confirm Delete";


}