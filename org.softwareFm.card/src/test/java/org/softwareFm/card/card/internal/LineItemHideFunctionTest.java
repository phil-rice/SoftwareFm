package org.softwareFm.card.card.internal;

import junit.framework.TestCase;

import org.softwareFm.card.card.LineItem;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class LineItemHideFunctionTest extends TestCase {

	private IFunction1<String, IResourceGetter> resourceGetterFn;

	public void test() throws Exception {
		checkLineHide(false, "h1", null);
		checkLineHide(false, "h2", null);
		checkLineHide(false, "h3", null);

		checkLineHide(true, "h1", "one");
		checkLineHide(true, "h2", "one");
		checkLineHide(false, "h3", "one");
	}

	private void checkLineHide(boolean expected, String key, String cardType) throws Exception {
		assertEquals(expected, new LineItemHideFunction(resourceGetterFn, "hide").apply(new LineItem(cardType, key, "irrelevant")).booleanValue());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		resourceGetterFn = new IFunction1<String, IResourceGetter>() {
			@Override
			public IResourceGetter apply(String from) throws Exception {
				if ("one".equals(from))
					return new ResourceGetterMock("hide", "h1,h2");
				else
					return new ResourceGetterMock();
			}
		};

	}

}
