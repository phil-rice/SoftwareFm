package org.softwareFm.card.card.internal;

import org.softwareFm.card.card.LineItem;

public class CardValueFunctionTest extends AbstractResourceGetterFnTest{

	private CardValueFunction function;

	public void testFunctionUsesPatternInResourceIfRelevantKey0IsKey1IsValue() throws Exception {
		assertEquals("aa1", function.apply(null, new LineItem("one", "a", "value")));
		assertEquals("bvalue1", function.apply(null, new LineItem("one", "b", "value")));
		assertEquals("aa2", function.apply(null, new LineItem("two", "a", "value")));
		assertEquals("bvalue2", function.apply(null, new LineItem("two", "b", "value")));
		assertEquals("aax", function.apply(null, new LineItem(null, "a", "value")));
		assertEquals("bvaluex", function.apply(null, new LineItem(null, "b", "value")));
	}

	public void testFunctionProvidesValueIfNoKey() throws Exception {
		assertEquals("value", function.apply(null, new LineItem(null, "q", "value")));
		assertEquals("value", function.apply(null, new LineItem(null, "camelCase", "value")));
	}
	
	
	public void testReplacesColonWIthUnderscores() throws Exception{
		assertEquals("xwi_th,value2", function.apply(null, new LineItem("one", "wi:th", "value")));
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		function = new CardValueFunction(resourceGetterFn, "x{0}");
	}
}
