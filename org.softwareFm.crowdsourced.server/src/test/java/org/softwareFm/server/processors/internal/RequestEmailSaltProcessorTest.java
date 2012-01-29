package org.softwareFm.server.processors.internal;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.maps.Maps;

public class RequestEmailSaltProcessorTest extends AbstractProcessCallTest<RequestEmailSaltProcessor> {

	private final String uri = "/" + CommonConstants.emailSaltPrefix;

	public void testIgnoresNonePrefix(){
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.GET, uri);
	}

	public void testEmail() {
		RequestLineMock requestLine = new RequestLineMock(CommonConstants.POST, uri);
		IProcessResult result = processor.process(requestLine,  Maps.stringObjectMap(LoginConstants.emailKey, "someEmail"));
		checkStringResult(result, "theSalt");
	}

	@Override
	protected RequestEmailSaltProcessor makeProcessor() {
		return new RequestEmailSaltProcessor(new EmailSailRequesterMock("theSalt"));
	}

}
