package org.softwareFm.server.user.internal;

import java.util.Map;

import javax.sql.DataSource;

import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.utilities.functions.IFunction1;

public class UserCryptoFn extends AbstractLoginDataAccessor implements IFunction1<Map<String,Object>, String>{

	public UserCryptoFn(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public String apply(Map<String, Object> from) throws Exception {
		String softwareFmId = (String) from.get(LoginConstants.softwareFmIdKey);
		if (softwareFmId == null)
			throw new NullPointerException(from.toString());
		String crypto = template.queryForObject(selectCryptoForSoftwareFmIdsql, String.class, softwareFmId);
		if (crypto == null)
			throw new NullPointerException(from.toString());
		return crypto;
	}

	
	
}
