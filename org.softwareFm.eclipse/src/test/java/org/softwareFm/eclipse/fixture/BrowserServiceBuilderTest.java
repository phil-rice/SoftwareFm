package org.softwareFm.eclipse.fixture;

import org.junit.Test;
import org.softwareFm.display.browser.IBrowserServiceBuilder;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.AbstractSimpleMapTest;
import org.softwareFm.utilities.maps.ISimpleMap;

public class BrowserServiceBuilderTest extends AbstractSimpleMapTest<String, IFunction1<String, String>> {

	@Test
	public void testThrowsExceptionIfKeyNotThere() {
		checkThrowsExceptionIfKeyNotThere("Unrecognised feed type b. Legal values are [a]");
	}

	@Override
	protected String makeKey(String seed) {
		return seed;
	}

	@Override
	protected IFunction1<String, String> makeValue(final String seed) {
		return new IFunction1<String, String>() {

			@Override
			public String apply(String from) throws Exception {
				throw new UnsupportedOperationException();
			}

			@Override
			public String toString() {
				return "Transformer: " + seed;
			}
		};
	}

	@Override
	protected ISimpleMap<String, IFunction1<String, String>> blankMap() {
		return new BrowserService();
	}

	@Override
	protected String duplicateKeyErrorMessage() {
		return "Cannot set value of feedType twice. Current value [Transformer: 1]. New value [a]";
	}

	@Override
	protected void put(ISimpleMap<String, IFunction1<String, String>> map, String key, IFunction1<String, String> value) {
		assertSame(map, ((IBrowserServiceBuilder) map).register(key, value));

	}

}
