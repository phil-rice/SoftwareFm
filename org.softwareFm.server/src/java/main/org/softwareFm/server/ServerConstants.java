package org.softwareFm.server;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.CommonMessages;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.utilities.url.IUrlGenerator;

public class ServerConstants {
	public static final String usagePrefix = "command/Usage";
	/**
	 * @deprecated Use {@link CommonConstants#dataFileName} instead
	 */
	@Deprecated
	public final static String dataFileName = CommonConstants.dataFileName;
	/**
	 * @deprecated Use {@link CommonConstants#collectionType} instead
	 */
	@Deprecated
	public static final String collectionType = CommonConstants.collectionType;
	/**
	 * @deprecated Use {@link CommonConstants#typeTag} instead
	 */
	@Deprecated
	public static final String typeTag = CommonConstants.typeTag;
	/**
	 * @deprecated Use {@link CommonMessages#cannotCreateGitUnderSecondRepository} instead
	 */
	@Deprecated
	public static final String cannotCreateGitUnderSecondRepository = CommonMessages.cannotCreateGitUnderSecondRepository;
	/**
	 * @deprecated Use {@link CommonConstants#GET} instead
	 */
	@Deprecated
	public static final String GET = CommonConstants.GET;
	/**
	 * @deprecated Use {@link CommonConstants#POST} instead
	 */
	@Deprecated
	public static final String POST = CommonConstants.POST;
	/**
	 * @deprecated Use {@link CommonConstants#DELETE} instead
	 */
	@Deprecated
	public static final String DELETE = CommonConstants.DELETE;
	/**
	 * @deprecated Use {@link CommonConstants#HEAD} instead
	 */
	@Deprecated
	public static final String HEAD = CommonConstants.HEAD;
	/**
	 * @deprecated Use {@link CommonMessages#couldntStop} instead
	 */
	@Deprecated
	public static final String couldntStop = CommonMessages.couldntStop;
	/**
	 * @deprecated Use {@link CommonConstants#testTimeOutMs} instead
	 */
	@Deprecated
	public static final long clientTimeOut = CommonConstants.testTimeOutMs;
	/**
	 * @deprecated Use {@link CommonConstants#okStatusCode} instead
	 */
	@Deprecated
	public static final int okStatusCode = CommonConstants.okStatusCode;
	/**
	 * @deprecated Use {@link CommonConstants#notFoundStatusCode} instead
	 */
	@Deprecated
	public static final int notFoundStatusCode = CommonConstants.notFoundStatusCode;
	/**
	 * @deprecated Use {@link CommonConstants#dataParameterName} instead
	 */
	@Deprecated
	public static final String dataParameterName = CommonConstants.dataParameterName;

	/**
	 * @deprecated Use {@link CommonConstants#findRepositoryBasePrefix} instead
	 */
	@Deprecated
	public static final String findRepositoryBasePrefix = CommonConstants.findRepositoryBasePrefix;
	/**
	 * @deprecated Use {@link LoginConstants#makeSaltPrefix} instead
	 */
	@Deprecated
	public static final String makeSaltPrefix = LoginConstants.makeSaltPrefix;
	/**
	 * @deprecated Use {@link CommonConstants#makeRootPrefix} instead
	 */
	@Deprecated
	public static final String makeRootPrefix = CommonConstants.makeRootPrefix;
	/**
	 * @deprecated Use {@link LoginConstants#loginCommandPrefix} instead
	 */
	@Deprecated
	public static final String loginCommandPrefix = LoginConstants.loginCommandPrefix;
	/**
	 * @deprecated Use {@link LoginConstants#signupPrefix} instead
	 */
	@Deprecated
	public static final String signupPrefix = LoginConstants.signupPrefix;
	/**
	 * @deprecated Use {@link LoginConstants#forgottonPasswordPrefix} instead
	 */
	@Deprecated
	public static final String forgottonPasswordPrefix = LoginConstants.forgottonPasswordPrefix;
	/**
	 * @deprecated Use {@link LoginConstants#passwordResetLinkPrefix} instead
	 */
	@Deprecated
	public static final String passwordResetLinkPrefix = LoginConstants.passwordResetLinkPrefix;
	/**
	 * @deprecated Use {@link CommonConstants#emailSaltPrefix} instead
	 */
	@Deprecated
	public static final String emailSaltPrefix = CommonConstants.emailSaltPrefix;
	/**
	 * @deprecated Use {@link LoginConstants#changePasswordPrefix} instead
	 */
	@Deprecated
	public static final String changePasswordPrefix = LoginConstants.changePasswordPrefix;

	/**
	 * @deprecated Use {@link CommonConstants#testPort} instead
	 */
	@Deprecated
	public static final int testPort = CommonConstants.testPort;
	public static final String alreadyInCache = "<Already in Cache>";
	/**
	 * @deprecated Use {@link CommonMessages#notFoundMessage} instead
	 */
	@Deprecated
	public static final String notFoundMessage = CommonMessages.notFoundMessage;
	/**
	 * @deprecated Use {@link CommonMessages#foundMessage} instead
	 */
	@Deprecated
	public static final String foundMessage = CommonMessages.foundMessage;
	/**
	 * @deprecated Use {@link CommonConstants#DOT_GIT} instead
	 */
	@Deprecated
	public static final String DOT_GIT = CommonConstants.DOT_GIT;
	/**
	 * @deprecated Use {@link CommonMessages#madeRoot} instead
	 */
	@Deprecated
	public static final String madeRoot = CommonMessages.madeRoot;
	public static final String cannotPullWhenLocalRepositoryDoesntExist = "Cannot pull {0} when local repository doesn''t exist";
	public static final String cannotPostWhenLocalRepositoryDoesntExist = "Cannot post {0} when local repository doesn''t exist";
	/**
	 * @deprecated Use {@link CommonConstants#dataKey} instead
	 */
	@Deprecated
	public static final String dataKey = CommonConstants.dataKey;
	/**
	 * @deprecated Use {@link CommonConstants#repoUrlKey} instead
	 */
	@Deprecated
	public static final String repoUrlKey = CommonConstants.repoUrlKey;
	public static final String cannotParseReplyFromGet = "Cannot parse answer from get: {0}";
	public static final long staleCacheTime = 60 * 5 * 1000;// millisecond before need to pull again
	public static final long staleAboveRepositoryCacheTime = 60 * 60 * 5 * 1000;// millisecond before need to clear above repository cache (should never need to, but I think it won't hurt...)
	public static final long staleCacheTimeForTests = 500;// hopefully enough to remove randomness without slowing tests too much
	public static final String snipperTemplateKey = "snippet.template";
	public static final String errorInGit = "Error in git. Url {0}";
	public static final String deleting = "Deleting {0}";
	/**
	 * @deprecated Use {@link LoginMessages#cannotDelete} instead
	 */
	@Deprecated
	public static final String cannotDelete = LoginMessages.cannotDelete;
	/**
	 * @deprecated Use {@link LoginMessages#cannotClearDirectory} instead
	 */
	@Deprecated
	public static final String cannotClearDirectory = LoginMessages.cannotClearDirectory;
	public static final String cannotFindRepositoryFor = "Cannot find repository for {0}";
	/**
	 * @deprecated Use {@link CommonConstants#sessionSaltKey} instead
	 */
	@Deprecated
	public static final String sessionSaltKey = LoginConstants.sessionSaltKey;
	/**
	 * @deprecated Use {@link CommonConstants#emailSaltKey} instead
	 */
	@Deprecated
	public static final String emailSaltKey = LoginConstants.emailSaltKey;
	/**
	 * @deprecated Use {@link LoginConstants#passwordHashKey} instead
	 */
	@Deprecated
	public static final String passwordHashKey = LoginConstants.passwordHashKey;
	/**
	 * @deprecated Use {@link LoginConstants#newPasswordHashKey} instead
	 */
	@Deprecated
	public static final String newPasswordHashKey = LoginConstants.newPasswordHashKey;

	/**
	 * @deprecated Use {@link LoginConstants#emailKey} instead
	 */
	@Deprecated
	public static final String emailKey = LoginConstants.emailKey;
	/**
	 * @deprecated Use {@link LoginMessages#emailPasswordMismatch} instead
	 */
	@Deprecated
	public static final String emailPasswordMismatch = LoginMessages.emailPasswordMismatch;
	/**
	 * @deprecated Use {@link LoginConstants#cryptoKey} instead
	 */
	@Deprecated
	public static final String cryptoKey = LoginConstants.cryptoKey;
	/**
	 * @deprecated Use {@link LoginMessages#invalidSaltMessage} instead
	 */
	@Deprecated
	public static final String invalidSaltMessage = LoginMessages.invalidSaltMessage;
	/**
	 * @deprecated Use {@link LoginMessages#existingEmailAddress} instead
	 */
	@Deprecated
	public static final String existingEmailAddress = LoginMessages.existingEmailAddress;
	/**
	 * @deprecated Use {@link LoginMessages#existingSoftwareFmId} instead
	 */
	@Deprecated
	public static final String existingSoftwareFmId = LoginMessages.existingSoftwareFmId;
	/**
	 * @deprecated Use {@link LoginMessages#duplicateEmailAndPassword} instead
	 */
	@Deprecated
	public static final String duplicateEmailAndPassword = LoginMessages.duplicateEmailAndPassword;
	/**
	 * @deprecated Use {@link LoginMessages#forgottonPasswordMessage} instead
	 */
	@Deprecated
	public static final String forgottonPasswordMessage = LoginMessages.forgottonPasswordMessage;
	/**
	 * @deprecated Use {@link LoginMessages#passwordResetSubject} instead
	 */
	@Deprecated
	public static final String passwordResetSubject = LoginMessages.passwordResetSubject;
	/**
	 * @deprecated Use {@link LoginMessages#emailAddressNotFound} instead
	 */
	@Deprecated
	public static final String emailAddressNotFound = LoginMessages.emailAddressNotFound;
	/**
	 * @deprecated Use {@link LoginMessages#passwordResetHtml} instead
	 */
	@Deprecated
	public static final String passwordResetHtml = LoginMessages.passwordResetHtml;
	public static final String timedOut = "Timed out";
	/**
	 * @deprecated Use {@link LoginMessages#emailNotRecognised} instead
	 */
	@Deprecated
	public static final String emailNotRecognised = LoginMessages.emailNotRecognised;
	/**
	 * @deprecated Use {@link LoginMessages#wrongPassword} instead
	 */
	@Deprecated
	public static final String wrongPassword = LoginMessages.wrongPassword;
	/**
	 * @deprecated Use {@link LoginMessages#passwordChanged} instead
	 */
	@Deprecated
	public static final String passwordChanged = LoginMessages.passwordChanged;
	/**
	 * @deprecated Use {@link LoginConstants#projectCryptoKey} instead
	 */
	@Deprecated
	public static final String projectCryptoKey = LoginConstants.projectCryptoKey;
	/**
	 * @deprecated Use {@link LoginConstants#softwareFmIdKey} instead
	 */
	@Deprecated
	public static final String softwareFmIdKey = LoginConstants.softwareFmIdKey;

	public static IUrlGenerator projectGenerator() {
		return IUrlGenerator.Utils.generator("softwareFm/users/{0}/{1}/{2}/projects", LoginConstants.softwareFmIdKey);
	}


}
