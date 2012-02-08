package org.softwareFm.softwareFmServer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.server.processors.IForgottonPasswordMailer;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

public class TakeOnGroup extends AbstractCommandProcessor {

	private final IMailer mailer;
	private final JdbcTemplate template;
	private final ISignUpChecker checker;
	private final IForgottonPasswordMailer forgottonPassword;
	private final ISaltProcessor saltProcessor;
	private final Callable<String> cryptoGenerator;
	private final IUser user;
	private final IGroups groups;
	private final IFunction1<Map<String, Object>, String> cryptoFn;

	public TakeOnGroup(DataSource dataSource, IMailer mailer, IUser user, IGroups groups, ISignUpChecker checker, ISaltProcessor saltProcessor, IForgottonPasswordMailer forgottonPassword, IFunction1<Map<String, Object>, String> cryptoFn, Callable<String> cryptoGenerator) {
		super(null, CommonConstants.POST, GroupConstants.takeOnCommandPrefix);
		this.mailer = mailer;
		this.user = user;
		this.groups = groups;
		this.checker = checker;
		this.saltProcessor = saltProcessor;
		this.forgottonPassword = forgottonPassword;
		this.cryptoFn = cryptoFn;
		this.cryptoGenerator = cryptoGenerator;
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String groupName = (String) parameters.get(GroupConstants.groupNameKey);
		String emailPattern = (String) parameters.get(GroupConstants.takeOnEmailPattern);
		String memberListRaw = (String) parameters.get(GroupConstants.takeOnEmailListKey);
		String groupCryptoKey = Callables.call(cryptoGenerator);
		List<String> memberList = Strings.splitIgnoreBlanks(memberListRaw, ",");
		String groupId = UUID.randomUUID().toString();
		for (String email : memberList) {
			String softwareFmId;
			int existingEmailCount = template.queryForInt("select count(*) from users where email=?", email);
			switch (existingEmailCount) {
			case 0: {
				softwareFmId = UUID.randomUUID().toString();
				String salt = saltProcessor.makeSalt();
				saltProcessor.invalidateSalt(salt);
				checker.signUp(email, salt, "not set", softwareFmId);
				String magicString = forgottonPassword.process(email);
				String crypto = Callables.call(cryptoGenerator);
				template.update("update users set passwordResetKey=? , crypto=? where email=?", magicString, crypto, email);
				user.setUserProperty(softwareFmId, crypto, LoginConstants.emailKey, email);
				user.setUserProperty(softwareFmId, crypto, LoginConstants.monikerKey, Strings.split(email, '@').pre);
				user.setUserProperty(softwareFmId, crypto, SoftwareFmConstants.projectCryptoKey, Callables.call(cryptoGenerator));
				break;
			}
			case 1: {
				String magicString = forgottonPassword.process(email);
				template.update("update users set passwordResetKey=? , where email=?", magicString, email);
				softwareFmId = template.queryForObject("select softwarefmid from users where email=?", String.class, email);
				break;
			}
			default:
				throw new IllegalStateException(email);
			}
			String userCrypto = Functions.call(cryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.emailKey, email));
			String usersProjectCryptoKey = user.getUserProperty(softwareFmId, userCrypto, SoftwareFmConstants.projectCryptoKey);
			
			groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, SoftwareFmConstants.projectCryptoKey, usersProjectCryptoKey));
		}
		return IProcessResult.Utils.processString("");
	}
}
