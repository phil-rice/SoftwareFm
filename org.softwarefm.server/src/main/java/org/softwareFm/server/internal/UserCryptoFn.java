package org.softwareFm.server.internal;

import java.util.Map;

import javax.sql.DataSource;

import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;

public class UserCryptoFn extends AbstractLoginDataAccessor implements IFunction1<Map<String, Object>, String> {

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
