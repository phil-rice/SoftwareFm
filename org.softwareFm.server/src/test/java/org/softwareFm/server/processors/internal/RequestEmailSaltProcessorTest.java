package org.softwareFm.server.processors.internal;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.maps.Maps;

public class RequestEmailSaltProcessorTest extends AbstractProcessCallTest<RequestEmailSaltProcessor> {

	private final String uri = "/" + ServerConstants.emailSaltPrefix;

	public void testIgnoresNonePrefix(){
		checkIgnoresNonePosts();
		checkIgnores(ServerConstants.GET, uri);
	}

	public void testEmail() {
		RequestLineMock requestLine = new RequestLineMock(ServerConstants.POST, uri);
		IProcessResult result = processor.process(requestLine,  Maps.stringObjectMap(ServerConstants.emailKey, "someEmail"));
		checkStringResult(result, "theSalt");
	}

	@Override
	protected RequestEmailSaltProcessor makeProcessor() {
		return new RequestEmailSaltProcessor(new EmailSailRequesterMock("theSalt"));
	}

}
