package org.softwareFm.server.processors.internal;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class MakeSaltForLoginProcessorTest extends AbstractProcessCallTest<MakeSaltForLoginProcessor> {

	private SaltProcessorMock saltProcessor;

	public void testIgnoresGetsWithoutCommandPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(ServerConstants.GET);
	}

	public void testCreatesSalt() throws Exception {
		RequestLine requestLine = makeRequestLine(ServerConstants.GET, "/" + ServerConstants.makeSaltPrefix);
		IProcessResult result = processor.process(requestLine, Maps.emptyStringObjectMap());
		String salt = Lists.getOnly(saltProcessor.createdSalts);
		checkStringResult(result, salt);
		assertNotNull(result);


	}

	@Override
	protected MakeSaltForLoginProcessor makeProcessor() {
		saltProcessor = new SaltProcessorMock();
		return new MakeSaltForLoginProcessor(remoteGitServer, saltProcessor);
	}

}
