package org.softwareFm.eclipse.user;

import java.util.Map;

import junit.framework.Assert;

import org.softwareFm.common.IUser;
import org.softwareFm.common.maps.Maps;

public class UserMock implements IUser {

	private Map<String, Object> map = Maps.newMap();
	private final Map<String, Object> expectedUserDetails;
	private final String expectedCrypto;

	public UserMock(String expectedCrypto, Map<String, Object> expectedUserDetails, Object... namesAndValues) {
		this.map = Maps.stringObjectMap(namesAndValues);
		this.expectedCrypto = expectedCrypto;
		this.expectedUserDetails = expectedUserDetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserProperty(Map<String, Object> userDetails, String cryptoKey, String property) {
		Assert.assertEquals(expectedUserDetails, userDetails);
		Assert.assertEquals(expectedCrypto, cryptoKey);
		return (T) map.get(property);
	}

	@Override
	public <T> void setUserProperty(Map<String, Object> userDetails, String cryptoKey, String property, T value) {
		Assert.assertEquals(expectedUserDetails, userDetails);
		Assert.assertEquals(expectedCrypto, cryptoKey);
		map.put(property, value);
	}

	@Override
	public void refresh(Map<String, Object> userDetails) {
		throw new UnsupportedOperationException();
	}
}
