package org.softwareFm.eclipse.fixture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.browser.BrowserComposite;
import org.softwareFm.display.browser.IBrowserPart;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.AbstractRepositoryFacardTest;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class BrowserServiceTest extends AbstractRepositoryFacardTest {

	private String url;
	private BrowserComposite browserComposite;
	private Shell shell;
	private final List<IBrowserPart> parts = Lists.newList();

	public void testSetup() {
		assertEquals(0, parts.size());
	}

	public void testCompositePassedToBrowserPartCreator() {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator());
		assertEquals(browserComposite.getControl(), one.from);
	}

	public void testWithUsingUrl() throws InterruptedException, ExecutionException {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator(true));
		assertEquals(Arrays.asList(one), parts);
		String actual = browserComposite.processUrl("one", url).get();
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		assertNull( actual);
		assertEquals(1, one.count);
		assertEquals(url, one.url);
	}

	public void testAccessingUrlPassesResultToBrowserPart() throws Exception {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator());
		BrowserPartMock two = (BrowserPartMock) browserComposite.register("two", makeBrowserCreator());
		assertEquals(Arrays.asList(one, two), parts);
		String expected = "{'B':2,'A':1,'jcr:primaryType':'nt:unstructured'}".replaceAll("'", "\"");
		String actual = browserComposite.processUrl("one", url).get();
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		assertEquals(expected, actual);
		assertEquals(1, one.count);
		assertEquals(expected, one.reply);
		assertEquals(200, one.statusCode);
		assertEquals(0, two.count);
	}

	public void testAccessingUrlChangesVisibilityToOnlyRegisteredService() throws InterruptedException, ExecutionException {
		BrowserPartMock one = (BrowserPartMock) browserComposite.register("one", makeBrowserCreator());
		BrowserPartMock two = (BrowserPartMock) browserComposite.register("two", makeBrowserCreator());
		BrowserPartMock three = (BrowserPartMock) browserComposite.register("three", makeBrowserCreator());
		List<Control> all = Arrays.asList(one.getControl(), two.getControl(), three.getControl());
		checkVisibility("one", all, one);
		checkVisibility("two", all, two);
		checkVisibility("three", all, three);

	}

	private void checkVisibility(String feedType, List<Control> all, BrowserPartMock mock) throws InterruptedException, ExecutionException {
		browserComposite.processUrl(feedType, url).get();
		Swts.dispatchUntilQueueEmpty(shell.getDisplay());
		Composite parent = (Composite) browserComposite.getControl();
		assertEquals(Sets.set(all), Sets.makeSet(parent.getChildren()));
		assertEquals(mock.getControl(), parent.getChildren()[0]);
		// assertTrue(parent.getChildren()[0].isVisible()); //hard to assert this as it's not actually visible in the test...
		for (int i = 1; i < all.size(); i++)
			assertFalse(parent.getChildren()[i].isVisible());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = new Shell();
		String name = "tests/" + getClass().getSimpleName();
		facard.post(name, Maps.<String, Object> makeMap("A", 1, "B", 2), IResponseCallback.Utils.noCallback()).get();
		url = "http://" + HttpClientConstants.defaultHost + ":" + HttpClientConstants.defaultPort + "/" + name + ".json";
		browserComposite = new BrowserComposite(shell, SWT.NULL);
	}

	private IFunction1<Composite, IBrowserPart> makeBrowserCreator() {
		return makeBrowserCreator(false);
	}

	private IFunction1<Composite, IBrowserPart> makeBrowserCreator(final boolean useUrl) {
		return new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				BrowserPartMock result = new BrowserPartMock(from, useUrl);
				parts.add(result);
				return result;
			}
		};

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		shell.dispose();

	}
}
