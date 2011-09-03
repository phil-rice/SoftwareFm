package org.softwareFm.utilities.collections;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.utilities.tests.Tests;

public class ReusableSimpleListTest extends TestCase {

	public void testAdd() {
		IReusableSimpleList<Integer> list = new ReusableSimpleList<Integer>();
		assertEquals(0, list.size());
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		assertEquals(Arrays.asList(1, 2, 3, 4), SimpleLists.asList(list));
		assertEquals(4, list.size());
	}

	public void testAddAfterClear() {
		IReusableSimpleList<Integer> list = new ReusableSimpleList<Integer>();
		assertEquals(0, list.size());
		list.add(1);
		list.add(2);
		list.clear();
		assertEquals(0, list.size());
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
		assertEquals(Arrays.asList(3, 4, 5, 6), SimpleLists.asList(list));
		assertEquals(4, list.size());
	}

	public void testGetAllowingOldAllowsAccessToLegalAndOldValues() {
		IReusableSimpleList<Integer> list = new ReusableSimpleList<Integer>();
		assertEquals(0, list.size());
		list.add(1);
		list.add(2);
		assertEquals(1, list.getAllowingOld(0).intValue());
		assertEquals(2, list.getAllowingOld(1).intValue());
		list.clear();
		assertEquals(0, list.size());
		assertEquals(1, list.getAllowingOld(0).intValue());
		assertEquals(2, list.getAllowingOld(1).intValue());
	}

	public void testGetThrowsExceptionIfIndexTooHigh() {
		IReusableSimpleList<Integer> list = new ReusableSimpleList<Integer>();
		checkThrowsIndexOutOfBounds(list, 0);
		list.add(1);
		list.add(2);
		checkThrowsIndexOutOfBounds(list, 2);
		checkThrowsIndexOutOfBounds(list, -1);
		list.clear();
		checkThrowsIndexOutOfBounds(list, 0);
		list.add(1);
		checkThrowsIndexOutOfBounds(list, 1);
		list.add(2);
		checkThrowsIndexOutOfBounds(list, 2);
		list.add(3);
		checkThrowsIndexOutOfBounds(list, 3);
	}

	private void checkThrowsIndexOutOfBounds(final IReusableSimpleList<Integer> list, final int i) {
		Tests.assertThrows(IndexOutOfBoundsException.class, new Runnable() {
			public void run() {
				list.get(i);
			}
		});
	}
}
