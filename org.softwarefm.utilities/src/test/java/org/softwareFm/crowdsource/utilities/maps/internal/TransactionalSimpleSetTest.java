package org.softwareFm.crowdsource.utilities.maps.internal;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;
import org.softwareFm.crowdsource.utilities.collections.Sets;

public class TransactionalSimpleSetTest extends TestCase {

	private ITransactionalMutableSimpleSet<Integer> transactionalSimpleSet;

	public void testEmptyWhenCreated() {
		assertFalse(transactionalSimpleSet.contains(1));
		assertFalse(transactionalSimpleSet.contains(2));
	}

	public void testContainsAfterAdded() {
		transactionalSimpleSet.add(1);
		assertTrue(transactionalSimpleSet.contains(1));
		assertFalse(transactionalSimpleSet.contains(2));

		transactionalSimpleSet.add(2);
		assertTrue(transactionalSimpleSet.contains(1));
		assertTrue(transactionalSimpleSet.contains(2));
	}
	
	public void testNotContainedAfterRollback(){
		transactionalSimpleSet.add(1);
		transactionalSimpleSet.add(2);
		assertTrue(transactionalSimpleSet.contains(1));
		assertTrue(transactionalSimpleSet.contains(2));
		
		transactionalSimpleSet.rollback();
		assertFalse(transactionalSimpleSet.contains(1));
		assertFalse(transactionalSimpleSet.contains(2));
	}
	
	public void testContainedAfterCommit(){
		transactionalSimpleSet.add(1);
		transactionalSimpleSet.add(2);
		assertTrue(transactionalSimpleSet.contains(1));
		assertTrue(transactionalSimpleSet.contains(2));
		
		transactionalSimpleSet.commit();
		assertTrue(transactionalSimpleSet.contains(1));
		assertTrue(transactionalSimpleSet.contains(2));
	}
	
	public void testRollbackAfterCommitKeepsFirstCommits(){
		transactionalSimpleSet.add(1);
		transactionalSimpleSet.commit();
		transactionalSimpleSet.add(2);
		transactionalSimpleSet.rollback();
		
		assertTrue(transactionalSimpleSet.contains(1));
		assertFalse(transactionalSimpleSet.contains(2));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		transactionalSimpleSet = Sets.<Integer>newTransactionalSet();
	}

}
