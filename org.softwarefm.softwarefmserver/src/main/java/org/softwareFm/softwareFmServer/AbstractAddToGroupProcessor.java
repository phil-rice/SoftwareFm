package org.softwareFm.softwareFmServer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

public abstract class AbstractAddToGroupProcessor extends AbstractCommandProcessor {

	protected final ITakeOnProcessor takeOnProcessor;
	protected final IFunction1<String, String> emailToSfmId;
	protected final ISignUpChecker signUpChecker;
	protected final Callable<String> saltGenerator;
	protected final Callable<String> softwareFmIdGenerator;
	protected final IMailer mailer;

	public AbstractAddToGroupProcessor(IGitOperations gitOperations, String method, String prefix, ITakeOnProcessor takeOnProcessor, ISignUpChecker signUpChecker, IFunction1<String, String> emailToSfmId, Callable<String> saltGenerator, Callable<String> softwareFmIdGenerator, IMailer mailer) {
		super(gitOperations, method, prefix);
		this.takeOnProcessor = takeOnProcessor;
		this.signUpChecker = signUpChecker;
		this.emailToSfmId = emailToSfmId;
		this.saltGenerator = saltGenerator;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
		this.mailer = mailer;
	}

	protected void sendInvitationEmails(Map<String, Object> parameters, String groupName, List<String> memberList) {
		String from = (String) parameters.get(GroupConstants.takeOnFromKey);// this dude is now the admin
		String emailPattern = (String) parameters.get(GroupConstants.takeOnEmailPattern);
		String rawSubject = (String) parameters.get(GroupConstants.takeOnSubjectKey);
		for (String email : memberList) {
			String subject = replaceMarkers(rawSubject, groupName, email);
			String message = replaceMarkers(emailPattern, groupName, email);
			mailer.mail(from, email, subject, message);
		}
	}

	protected void addUsersToGroup(String groupId, String groupCrypto, List<String> memberList) {
		if (groupCrypto == null)
			throw new NullPointerException();
		for (String email : memberList) {
			String newSoftwareFmId = Functions.call(emailToSfmId, email);
			if (newSoftwareFmId == null) {
				newSoftwareFmId = Callables.call(softwareFmIdGenerator);
				String moniker = Strings.split(email, '@').pre;
				String salt = Callables.call(saltGenerator);
				SignUpResult signUpResult = signUpChecker.signUp(email, moniker, salt, "not set", newSoftwareFmId);
				if (signUpResult.errorMessage != null)
					throw new RuntimeException(signUpResult.errorMessage);
			}
			takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, newSoftwareFmId, email, GroupConstants.invitedStatus);
		}
	}

	protected List<String> getEmailList(Map<String, Object> parameters) {
		String memberListRaw = (String) parameters.get(GroupConstants.takeOnEmailListKey);
		List<String> memberList = Strings.splitIgnoreBlanks(memberListRaw, ",");
		for (String email : memberList)
			if (!Strings.isEmail(email))
				throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, email));
		return memberList;
	}

	protected String replaceMarkers(String rawMessage, String groupName, String email) {
		return rawMessage.replace(GroupConstants.emailMarker, email).replace(GroupConstants.groupNameMarker, groupName);
	}
}
