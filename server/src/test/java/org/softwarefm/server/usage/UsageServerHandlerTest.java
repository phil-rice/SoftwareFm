package org.softwarefm.server.usage;

import org.apache.http.HttpStatus;
import org.easymock.EasyMock;
import org.softwarefm.server.AbstractServerHandlerTest;
import org.softwarefm.server.usage.internal.UsageServerHandler;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageReporter;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.strings.Strings;

public class UsageServerHandlerTest extends AbstractServerHandlerTest<UsageServerHandler> {

	private IUsageCallbackAndGetter callbackAndGetter;
	private final IUsageReporter reporter = IUsageReporter.Utils.reporter("localhost", port);
	private IUsagePersistance persistance;

	public void testShutsDown() {
		EasyMock.replay(callbackAndGetter);
		httpServer.start();
	}

	public void testWithPostUsageIsDecodedAndSentToCallback() {
		callbackAndGetter.process("", "someUser", UsageTestData.statsa1b3);
		EasyMock.replay(callbackAndGetter);
		httpServer.start();

		reporter.report("someUser", UsageTestData.statsa1b3);
	}

	public void testWithGetUsageIsReturned() {
		EasyMock.expect(callbackAndGetter.getStats("someUser")).andReturn(UsageTestData.statsa1b3);
		EasyMock.replay(callbackAndGetter);
		httpServer.start();
		String usage = checkCall(httpClient.get("usage/someUser"));
		assertEquals(UsageTestData.statsa1b3, persistance.parse(usage));

	}

	public void testUsageDoesntDieWithBadXmlThenCanProcessGoodData() {
		checkUsageDoesntDie(Strings.zip("some \njunk\nthat isnt xml"));

	}

	public void testUsageDoesntDieWithNoneZipThenCanProcessGoodData() {
		checkUsageDoesntDie("some \njunk\nthat isnt a zip".getBytes());

	}

	private void checkUsageDoesntDie(byte[] badEntity) {
		callbackAndGetter.process("", "someUser", UsageTestData.statsa1b3);
		EasyMock.replay(callbackAndGetter);
		httpServer.start();

		IResponse response = IHttpClient.Utils.builder().host("localhost", port).withEntity(badEntity).post("").execute();
		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.statusCode());
		reporter.report("someUser", UsageTestData.statsa1b3);
	}

	@Override
	protected UsageServerHandler createHandler() {
		return new UsageServerHandler(persistance = IUsagePersistance.Utils.persistance(), callbackAndGetter = EasyMock.createMock(IUsageCallbackAndGetter.class));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(callbackAndGetter);
	}

}
