package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.maps.Maps;

public class ForgottonPasswordWebPageProcessorTest extends AbstractProcessCallTest<ForgottonPasswordProcessor> {

	private SaltProcessorMock saltProcessor;
	private ForgottonPasswordMailerMock forgottonPasswordMailer;
	private final String uri = "/" + CommonConstants.forgottonPasswordPrefix;

	public void testIgnoresNonePostsAndNonePrefix() {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.POST);
		checkIgnores(CommonConstants.GET, uri);
	}

	public void testSendsEmailToMailer(){
		RequestLineMock requestLine = new RequestLineMock(CommonConstants.POST, uri);
		Map<String, Object> data = Maps.stringObjectMap(LoginConstants.emailKey, "someEmail", LoginConstants.sessionSaltKey, "");
		IProcessResult result = processor.process(requestLine, data);
		checkStringResult(result, "");
	}
	@Override
	protected ForgottonPasswordProcessor makeProcessor() {
		saltProcessor = new SaltProcessorMock();
		forgottonPasswordMailer = new ForgottonPasswordMailerMock("someMagicString");
		return new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordMailer);
	}

}
