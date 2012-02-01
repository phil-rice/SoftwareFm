package org.softwareFm.server.processors.internal;

import junit.framework.TestCase;

import org.junit.Test;

public class SaltProcessorTest extends TestCase{

	@Test
	public void test() {
		SaltProcessor saltProcessor = new SaltProcessor();
		String uuid1 = saltProcessor.makeSalt();
		String uuid2 = saltProcessor.makeSalt();
		assertFalse(uuid1.equals(uuid2));
		
		assertTrue(saltProcessor.invalidateSalt(uuid1));
		assertTrue(saltProcessor.invalidateSalt(uuid2));
		
		assertFalse(saltProcessor.invalidateSalt(uuid1));
		assertFalse(saltProcessor.invalidateSalt(uuid2));
	}

}
