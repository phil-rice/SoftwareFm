/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.constants;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.crowdsource.utilities.constants.LoginConstants;

public class CardConstants {
	public static final String softwareFmPrefix = "/softwareFm";
	public static final String dataPrefix = "data";
	public static final String buttonTestTitle = "button.test.title";

	public static final String cardHolderLoadingText = "card.holder.loading.text";
	public static final String cardContentField = "card.content.field";
	public static final String cardContentUrl = "card.content.url";
	public static final String cardContentFeedType = "card.content.feedType";

	public static final String slingResourceType = "sling:resourceType";
	public static final String cannotFindTableItemWithKey = "Cannot find table item with key {0}";
	public static final String exceptionChangingValue = "Exception changing value with key {0} and newValue {1})";
	public static final String ntUnstructured = "nt:unstructured";
	public static final String jcrPrimaryType = "jcr:primaryType";

	public static final Object found = "found";
	public static final String groupUrlKey = "urlGenerator.group";
	public static final String artifactUrlKey = "urlGenerator.artifact";
	public static final String snippetUrlKey = "urlGenerator.snippet";
	public static final String jarNameUrlKey = "urlGenerator.jarName";
	public static final String versionCollectionUrlKey = "urlGenerator.versionCollection";
	public static final String versionUrlKey = "urlGenerator.version";
	public static final String digestUrlKey = "urlGenerator.digest";
	public static final String jarUrlRootKey = "urlGenerator.jar.root";
	public static final String jarUrlKey = "urlGenerator.jar";
	public static final String userUrlKey = "urlGenerator.user";
	public static final String projectUrlKey = "urlGenerator.project";
	public static final String manuallyAdded = "manually added";

	public static final String menuItemViewActual = "menuitem.view.actual";
	public static final String menuItemViewCards = "menuitem.view.cards";
	public static final String menuItemAddCollection = "menuitem.add.collection";
	public static final String menuItemAddArtifact = "menuitem.add.artifact";
	public static final String menuItemAddSnippet = "menuitem.add.snippet";
	public static final String menuItemViewText = "menuitem.view.text";
	public static final String menuItemEditText = "menuitem.edit.text";
	public static final String menuItemjavadoc = "menuitem.javadoc";
	public static final String menuItemEditSnippetText = "menuitem.editSnippet.text";
	public static final String menuItemViewSnippetText = "menuitem.viewSnippet.text";
	public static final String menuItemBrowseText = "menuitem.browse.text";
	public static final String menuItemExternalBrowseText = "menuitem.externalBrowse.text";
	public static final String menuItemHelpText = "menuitem.help.text";

	public static final String menuItemBrowseJarKey = "menuitem.browse.jar";
	public static final String menuItemBrowseJarMavenKey = "menuitem.browseMaven.jar";
	public static final String menuItemSearchSoftwareFmForJar = "menuItem.searchSoftwareFm.jar";

	public static final String cardColorKey = "card.color";
	public static final String cardTitleColorKey = "card.title.color";
	public static final String indentTitleKey = "card.title.indent";
	public static final String cardTitleIcon = "card.title.icon";
	public static final String navIcon = "card.title.navIcon";
	public static final String missingStringKey = "card.missing.string";
	public static final String missingListKey = "card.missing.list";
	public static final String cardOrderKey = "card.order";
	public static final String cardNameUrlKey = "card.name.url";
	public static final String cardNameFieldKey = "card.name.field";

	public static final String keysHideKey = "keys.hide";
	public static final String namePattern = "{0}.name";
	public static final String valuePattern = "{0}.value";

	public static final String cardAggregatorTagKey = "card.aggregator.tag";

	public static final String folder = "Folder";
	public static final String group = "group";
	public static final String artifact = "artifact";

	public static final String versionJar = "jar";
	public static final String collection = "collection";
	public static final String snippet = "snippet";
	public static final String jarName = "jarname";

	public static final String webPageWelcomeUrl = "webpage.welcome.url";
	public static final String webPageGroupUrl = "webpage.group.url";
	public static final String webPageArtifactUrl = "webpage.artifact.url";
	public static final String webPageVersionUrl = "webpage.version.url";
	public static final String webPageJarUrl = "webpage.jar.url";
	public static final String webPageSnippetUrl = "webpage.snippet.url";
	public static final String webPageDebugUrl = "webpage.debug.url";

	public static final String webPageUnknownJarUrl = "webpage.unknownJar.url";
	public static final String urlRogueValue = "<key>";
	public static final String couldNotGetImagefor = "Could not get image for {0}";
	public static final String cannotStyleText = "Cannot style text [{0}] because of character at {1} which is a '{2}'";

	public static final String loginCardType = "login";
	public static final String signupCardType = "signup";
	public static final String forgotPasswordCardType = "forgotpassword";
	public static final String loginTitle = "login.title.text";
	public static final String forgotPasswordTitle = "forgotPassword.title.text";
	public static final String forgotPasswordMessage = "forgotPassword.message.text";
	public static final String signupTitle = "signup.title.text";

	public static final String signUpButtonTitle = "signup.button.text";
	public static final String loginButtonTitle = "login.button.text";
	public static final String forgotPasswordButtonTitle = "forgotPassword.button.text";

	public static final String contactingServerTitle = "login.contactingServer.title";
	public static final String contactingServerText = "login.contactingServer.text";

	public static final String failedToSendForgottenPasswordTitle = "forgotpassword.failedToSend.title";
	public static final String failedToSendForgottenPasswordText = "forgotpassword.failedToSend.text";

	public static final String sentForgottenPasswordTitle = "forgotpassword.emailSent.title";
	public static final String sentForgottenPasswordText = "forgotpassword.emailSent.text";

	public static final String loggedInTitle = "login.loggedIn.title";
	public static final String loggedInText = "login.loggedIn.text";

	public static final String failedToContactServerTitle = "login.failedtocontactserver.title";
	public static final String failedToContactServerText = "login.failedtocontactserver.text";

	public static final String failedToLoginTitle = "login.failedtologin.title";
	public static final String failedToLoginText = "login.failedtologin.text";

	public static final String signedUpInTitle = "signup.signedup.title";
	public static final String signedUpText = "signup.signedup.text";

	public static final String failedToSignupTitle = "signup.failedtosignup.title";
	public static final String failedToSignupText = "signup.failedtosignup.text";

	public static final String changePasswordCardType = "changepassword";
	public static final String changedPasswordTitle = "changepassword.changedpassword.title";
	public static final String changedPasswordText = "changepassword.changedpassword.text";
	public static final String failedToChangePasswordTitle = "changepassword.failedtochangepassword.title";
	public static final String failedToChangePasswordText = "changepassword.failedtochangepassword.text";
	public static final String changePasswordTitle = "changepassword.title.text";
	public static final String myDetailsCardType = "myDetails";
	public static final List<String> mySoftwareFmDisplayProperties = Arrays.asList(LoginConstants.emailKey, LoginConstants.monikerKey);
	public static final String threadMonitorCardType = "threads";
}