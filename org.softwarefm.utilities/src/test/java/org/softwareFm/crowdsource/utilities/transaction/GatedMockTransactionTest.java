package org.softwareFm.crowdsource.utilities.transaction;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.Functions.ConstantFunctionWithMemoryOfFroms;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class GatedMockTransactionTest extends TestCase {

	@Test
	public void testMockReturnsValueOnceKicked() {
		GatedTransaction<String> mock = new GatedTransaction<String>("value");
		mock.kick();
		assertEquals("value", mock.get());
		assertEquals("value", mock.get(1));
	}
	
	public void testMockAppliesFUnctionOnkock(){
		ConstantFunctionWithMemoryOfFroms<Object, String> fn = new Functions.ConstantFunctionWithMemoryOfFroms<Object, String>("value");
		GatedTransaction<String> mock = new GatedTransaction<String>(fn, "from");
		assertEquals(0, fn.froms.size());
		mock.kick();
		assertEquals("from", Lists.getOnly(fn.froms));
		assertEquals("value", mock.get(1));
		
	}

	public void testMockDoesntReturnUntilKick() {
		final GatedTransaction<String> mock = new GatedTransaction<String>("value");
		Tests.assertThrows(WrappedException.class, new Runnable() {
			@Override
			public void run() {
				mock.get(10);
			}
		});
		mock.kick();
		assertEquals("value", mock.get(10));
	}
	
	public void testIsntDoneUntilKick(){
		final GatedTransaction<String> mock = new GatedTransaction<String>("value");
		assertFalse(mock.isDone());
		mock.kick();
		assertTrue(mock.isDone());
	}
}
