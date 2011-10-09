package org.softwareFm.eclipse.fixture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.browser.BrowserComposite;
import org.softwareFm.display.browser.IBrowserPart;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.AbstractRepositoryFacardTest;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class BrowserServiceTest extends AbstractRepositoryFacardTest {

	private String url;
	private BrowserComposite browserComposite;
	private Shell shell;
	private final List<IBrowserPart> parts = Lists.newList();
	private ExecutorService service;

	public void testSetup() {
		assertEquals(0, parts.size());
	}

	public void testCompositePassedToBrowserPartCreator() {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator("one"));
		assertEquals(browserComposite.getControl(), one.from);
	}

	public void testWithUsingUrl() throws InterruptedException, ExecutionException {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator("one", true));
		assertEquals(Arrays.asList(one), parts);
		assertEquals(0, one.count);
		String actual = browserComposite.processUrl("one", url).get();
		assertEquals(0, one.count);
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		assertNull( actual);
		assertEquals(1, one.count);
		assertEquals(1, one.replyCount);
		assertEquals(url, one.url);
	}

	public void testAccessingUrlPassesResultToBrowserPart() throws Exception {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator("one"));
		BrowserPartMock two = (BrowserPartMock) browserComposite.register("two", makeBrowserCreator("two"));
		assertEquals(Arrays.asList(one, two), parts);
		String expected = "{'B':2,'A':1,'jcr:primaryType':'nt:unstructured'}".replaceAll("'", "\"");
		String actual = browserComposite.processUrl("one", url).get();
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		assertEquals(expected, actual);
		assertEquals(0, one.count);
		assertEquals(1, one.replyCount);
		assertEquals(expected, one.reply);
		assertEquals(200, one.statusCode);
		assertEquals(0, two.count);
	}

	public void testAccessingUrlChangesVisibilityToOnlyRegisteredService() throws InterruptedException, ExecutionException {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator("one"));
		BrowserPartMock two = (BrowserPartMock) browserComposite.register("two", makeBrowserCreator("two"));
		BrowserPartMock three = (BrowserPartMock) browserComposite.register("three", makeBrowserCreator("three"));
		checkVisibility("one", one);
		checkVisibility("two", two);
		checkVisibility("three", three);

	}

	private void checkVisibility(String feedType, BrowserPartMock mock) throws InterruptedException, ExecutionException {
		browserComposite.processUrl(feedType, url).get();
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		assertEquals(mock.getControl(), browserComposite.getStackLayoutTopControlForTests());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = new Shell();
		String name = "tests/" + getClass().getSimpleName();
		facard.post(name, Maps.<String, Object> makeMap("A", 1, "B", 2), IResponseCallback.Utils.noCallback()).get();
		url = "http://" + HttpClientConstants.defaultHost + ":" + HttpClientConstants.defaultPort + "/" + name + ".json";
		service = new ThreadPoolExecutor(2, 10, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
		browserComposite = new BrowserComposite(shell, SWT.NULL, service);
	}

	private IFunction1<Composite, IBrowserPart> makeBrowserCreator(String name) {
		return makeBrowserCreator(name, false);
	}

	private IFunction1<Composite, IBrowserPart> makeBrowserCreator(final String name, final boolean useUrl) {
		return new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				BrowserPartMock result = new BrowserPartMock(from, name, useUrl);
				parts.add(result);
				return result;
			}
		};

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		service.shutdown();
		shell.dispose();

	}
}
