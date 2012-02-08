package org.softwareFm.softwareFmServer;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.springframework.jdbc.core.JdbcTemplate;

public class TakeOnProcessor implements ITakeOnProcessor {

	private final JdbcTemplate template;
	private final ISignUpChecker checker;
	private final ISaltProcessor saltProcessor;
	private final Callable<String> cryptoGenerator;
	private final IUser user;
	private final IGroups groups;
	private final IFunction1<Map<String, Object>, String> cryptoFn;
	private final Callable<String> softwareFmIdGenerator;

	public TakeOnProcessor(DataSource dataSource, IUser user, IGroups groups, ISignUpChecker checker, ISaltProcessor saltProcessor, Callable<String> softwareFmIdGenerator, IFunction1<Map<String, Object>, String> cryptoFn, Callable<String> cryptoGenerator) {
		this.user = user;
		this.groups = groups;
		this.checker = checker;
		this.saltProcessor = saltProcessor;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
		this.cryptoFn = cryptoFn;
		this.cryptoGenerator = cryptoGenerator;
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public String createUser(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addExistingUserToGroup(String groupId, String groupName, String email) {
		// TODO Auto-generated method stub

	}

}
