package org.softwareFm.softwareFmServer;

import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

public class SoftwareFmServerTest extends GitTest {
	private final String cryptoKey = Crypto.makeKey();

	public void testCreatesMembershipAndGroupCrypto() {
		checkPropertyIsCreatedAndIsCrypto(GroupConstants.membershipCryptoKey);
		checkPropertyIsCreatedAndIsCrypto(GroupConstants.groupCryptoKey);
		checkPropertyIsCreatedAndIsCrypto(SoftwareFmConstants.projectCryptoKey);
	}

	protected void checkPropertyIsCreatedAndIsCrypto(String key) {
		IUser user = SoftwareFmServer.makeUser(remoteOperations, LoginConstants.userGenerator());
		user.setUserProperty("someId", cryptoKey, "needed to", "create user");

		String crypto1 = user.getUserProperty("someId", cryptoKey, key);
		String crypto2 = user.getUserProperty("someId", cryptoKey, key);
		assertNotNull(crypto1);
		assertEquals(crypto1, crypto2);

		String encrypted = Crypto.aesEncrypt(crypto1, "value");
		assertEquals("value", Crypto.aesDecrypt(crypto1, encrypted));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
}
