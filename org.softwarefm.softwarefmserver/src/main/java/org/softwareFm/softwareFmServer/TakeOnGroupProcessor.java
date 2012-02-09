package org.softwareFm.softwareFmServer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

public class TakeOnGroupProcessor extends AbstractCommandProcessor {

	private final ITakeOnProcessor takeOnProcessor;
	private final Callable<String> cryptoGenerator;
	private final IFunction1<String, String> emailToSfmId;
	private final ISignUpChecker signUpChecker;
	private final Callable<String> saltGenerator;
	private final Callable<String> softwareFmIdGenerator;
	private final IMailer mailer;

	public TakeOnGroupProcessor(ITakeOnProcessor takeOnProcessor, ISignUpChecker signUpChecker, Callable<String> cryptoGenerator, IFunction1<String, String> emailToSfmId, Callable<String> saltGenerator, Callable<String> softwareFmIdGenerator, IMailer mailer) {
		super(null, CommonConstants.POST, GroupConstants.takeOnCommandPrefix);
		this.takeOnProcessor = takeOnProcessor;
		this.signUpChecker = signUpChecker;
		this.cryptoGenerator = cryptoGenerator;
		this.emailToSfmId = emailToSfmId;
		this.saltGenerator = saltGenerator;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
		this.mailer = mailer;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		check(parameters, GroupConstants.groupNameKey, GroupConstants.takeOnEmailPattern, GroupConstants.takeOnFromKey, GroupConstants.takeOnSubjectKey, GroupConstants.takeOnEmailListKey);
		String groupName = (String) parameters.get(GroupConstants.groupNameKey);
		String emailPattern = (String) parameters.get(GroupConstants.takeOnEmailPattern);
		String from = (String) parameters.get(GroupConstants.takeOnFromKey);
		String subject = (String) parameters.get(GroupConstants.takeOnSubjectKey);
		String memberListRaw = (String) parameters.get(GroupConstants.takeOnEmailListKey);

		String groupCryptoKey = Callables.call(cryptoGenerator);
		List<String> memberList = Strings.splitIgnoreBlanks(memberListRaw, ",");
		for (String email : memberList) 
			if (!Strings.isEmail(email))
				throw new IllegalArgumentException(email);
		String groupId = takeOnProcessor.createGroup(groupName, groupCryptoKey);
		for (String email : memberList) {
			String softwareFmId = Functions.call(emailToSfmId, email);
			if (softwareFmId == null) {
				softwareFmId = Callables.call(softwareFmIdGenerator);
				String moniker = Strings.split(email, '@').pre;
				String salt = Callables.call(saltGenerator);
				SignUpResult signUpResult = signUpChecker.signUp(email, moniker, salt, "not set", softwareFmId);
				if (signUpResult.errorMessage != null)
					throw new RuntimeException(signUpResult.errorMessage);
			}
			takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCryptoKey, email);
			String message = emailPattern.replace(GroupConstants.emailMarker, email);
			mailer.mail(from, email, subject, message);

		}
		return IProcessResult.Utils.processString("");
	}

	private void check(Map<String, Object> parameters, String... keys) {
		for (String key : keys)
			if (!parameters.containsKey(key))
				throw new IllegalArgumentException(key+", " + parameters);
	}
}
