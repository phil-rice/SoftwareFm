package org.softwareFm.card.card.internal;

import org.softwareFm.card.card.LineItem;

public class CardNameFunctionTest extends AbstractResourceGetterFnTest {

	private CardNameFunction function;

	public void testFunctionUsesPatternInResourceIfRelevantKey0IsRawKey1IsPretty() throws Exception {
		assertEquals("aa1", function.apply(null, new LineItem("one", "a", "irrelevant")));
		assertEquals("bB1", function.apply(null, new LineItem("one", "b", "irrelevant")));
		assertEquals("aa2", function.apply(null, new LineItem("two", "a", "irrelevant")));
		assertEquals("bB2", function.apply(null, new LineItem("two", "b", "irrelevant")));
		assertEquals("aax", function.apply(null, new LineItem(null, "a", "irrelevant")));
		assertEquals("bBx", function.apply(null, new LineItem(null, "b", "irrelevant")));
	}

	public void testFunctionPrettyPrintsIfNoKey() throws Exception {
		assertEquals("Q", function.apply(null, new LineItem(null, "q", "irrelevant")));
		assertEquals("Camel Case", function.apply(null, new LineItem(null, "camelCase", "irrelevant")));
	}
	
	
	public void testReplacesColonWIthUnderscores() throws Exception{
		assertEquals("xwi_th,Wi:th2", function.apply(null, new LineItem("one", "wi:th", "irrelevant")));
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		function = new CardNameFunction(resourceGetterFn, "x{0}");
	}

}
