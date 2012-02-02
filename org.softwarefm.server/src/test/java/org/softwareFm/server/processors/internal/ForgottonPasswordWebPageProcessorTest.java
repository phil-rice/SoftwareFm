package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.IProcessResult;

public class ForgottonPasswordWebPageProcessorTest extends AbstractProcessCallTest<ForgottonPasswordProcessor> {

	private SaltProcessorMock saltProcessor;
	private ForgottonPasswordMailerMock forgottonPasswordMailer;
	private final String uri = "/" + LoginConstants.forgottonPasswordPrefix;

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