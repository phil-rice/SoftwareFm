package org.softwareFm.server.processors.internal;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IPasswordChanger;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.maps.Maps;

public class ChangePasswordProcessorTest extends AbstractProcessCallTest<ChangePasswordProcessor> {

	private final static String url = "/" + ServerConstants.changePasswordPrefix;

	private PasswordChangerMock passwordChanger;

	public void testIgnoresGetsAndNoneCommands() {
		checkIgnoresNonePosts();
		checkIgnores(ServerConstants.GET, url);
	}

	public void testResetsPassword() {
		checkCalls("email", "hash", "newHash", true);
		checkCalls("email", "hash", "newHash", false);
	}

	private void checkCalls(String email, String oldHash, String newHash, boolean expected) {
		passwordChanger.setOk(expected);
		IProcessResult result = processor.process(new RequestLineMock(ServerConstants.POST, url), Maps.stringObjectMap(ServerConstants.emailKey, email, ServerConstants.passwordHashKey, oldHash, ServerConstants.newPasswordHashKey, newHash));
		if (expected)
			checkStringResult(result, ServerConstants.passwordChanged);
		else
			checkErrorResult(result, ServerConstants.notFoundStatusCode, ServerConstants.wrongPassword, ServerConstants.wrongPassword);
	}

	@Override
	protected ChangePasswordProcessor makeProcessor() {
		passwordChanger = IPasswordChanger.Utils.mockPasswordChanger();
		return new ChangePasswordProcessor(passwordChanger);
	}

}
