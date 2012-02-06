package org.softwareFm.eclipse.user;

import java.util.Map;

import junit.framework.Assert;

import org.softwareFm.common.IUser;
import org.softwareFm.common.maps.Maps;

public class UserMock implements IUser {

	private Map<String, Object> map = Maps.newMap();
	private final String expectedUserDetails;
	private final String expectedCrypto;

	public UserMock(String expectedCrypto, String softwareFmId, Object... namesAndValues) {
		this.map = Maps.stringObjectMap(namesAndValues);
		this.expectedCrypto = expectedCrypto;
		this.expectedUserDetails = softwareFmId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserProperty(String softwareFmId, String cryptoKey, String property) {
		Assert.assertEquals(expectedUserDetails, softwareFmId);
		Assert.assertEquals(expectedCrypto, cryptoKey);
		return (T) map.get(property);
	}

	@Override
	public <T> void setUserProperty(String softwareFmId, String cryptoKey, String property, T value) {
		Assert.assertEquals(expectedUserDetails, softwareFmId);
		Assert.assertEquals(expectedCrypto, cryptoKey);
		map.put(property, value);
	}

	@Override
	public void refresh(String softwareFmId) {
	}
}
