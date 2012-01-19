package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.maps.Maps;

public class ForgottonPasswordWebPageProcessorTest extends AbstractProcessCallTest<ForgottonPasswordProcessor> {

	private SaltProcessorMock saltProcessor;
	private ForgottonPasswordMailerMock forgottonPasswordMailer;
	private final String uri = "/" + ServerConstants.forgottonPasswordPrefix;

	public void testIgnoresNonePostsAndNonePrefix() {
		checkIgnoresNonePosts();
		checkIgnores(ServerConstants.POST);
		checkIgnores(ServerConstants.GET, uri);
	}

	public void testSendsEmailToMailer(){
		RequestLineMock requestLine = new RequestLineMock(ServerConstants.POST, uri);
		Map<String, Object> data = Maps.stringObjectMap(ServerConstants.emailKey, "someEmail", ServerConstants.sessionSaltKey, "");
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
