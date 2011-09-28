package org.softwareFm.eclipse.fixture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.display.browser.IBrowserCallback;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.AbstractRepositoryFacardTest;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class BrowserServiceTest extends AbstractRepositoryFacardTest {

	private String url;
	private BrowserService browserService;

	public void testAccessingUrlWithIdentity() throws Exception {
		browserService.register("identity", Functions.<String, String> identity());
		checkGet("identity", "{'B':2,'A':1,'jcr:primaryType':'nt:unstructured'}".replaceAll("'", "\""));
	}
	public void testAccessingUrlWithTransformation() throws Exception {
		browserService.register("transform", new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return "__" + from + "__";
			}
		});
		checkGet("transform", "__{'B':2,'A':1,'jcr:primaryType':'nt:unstructured'}__".replaceAll("'", "\""));
	}

	private void checkGet(String feedType, final String expected) throws InterruptedException, ExecutionException {
		final AtomicInteger count = new AtomicInteger();
		String actual = browserService.processUrl(feedType, url, new IBrowserCallback() {
			@Override
			public void process(int statusCode, String page) {
				assertEquals(200, statusCode);
				assertEquals(expected, page);
				count.incrementAndGet();
			}
		}).get();
		assertEquals(expected, actual);
		assertEquals(1, count.get());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String name = "tests/" + getClass().getSimpleName();
		facard.post(name, Maps.<String, Object> makeMap("A", 1, "B", 2), IResponseCallback.Utils.noCallback()).get();
		url = "http://" + HttpClientConstants.defaultHost + ":" + HttpClientConstants.defaultPort + "/" + name + ".json";
		browserService = new BrowserService();
	}
}
