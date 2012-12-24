package org.softwarefm.utilities.events.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.events.IValid;
import org.softwarefm.utilities.exceptions.MultipleExceptions;
import org.softwarefm.utilities.tests.Tests;

public class ListenerListTest extends TestCase {

	static interface ITestListener {
	}

	static interface ITestListenerAndValid extends ITestListener, IValid {
	}

	private IMultipleListenerList rawList;
	private final Object source = new Object();
	private final Object invalidSource = new Object();
	private ITestListener mockL1;
	private ITestListener mockL2;
	private ITestListener mockInvalid1;
	private ITestListener mockInvalid2;
	private ITestListenerAndValid mockV1;
	private ITestListenerAndValid mockV2;
	private ListenerList<ITestListener> list;

	public void testCallsFireOnAllListeners() {
		list.addListener(mockL1);
		list.addListener(mockL2);
		replay();

		final List<ITestListener> actual = Collections.synchronizedList(new ArrayList<ITestListener>());
		list.fire(new ICallback<ITestListener>() {
			public void process(ITestListener listener) throws Exception {
				actual.add(listener);
			}
		});
		verify();
		assertEquals(Arrays.asList(mockL1, mockL2), actual);
	}

	public void testCallsFireOnAllListenersEvenIfExceptionThenThrowsMultipleException() {
		list.addListener(mockL1);
		list.addListener(mockL2);
		replay();

		final List<ITestListener> actual = Collections.synchronizedList(new ArrayList<ITestListener>());
		final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());

		MultipleExceptions actualException = Tests.assertThrows(MultipleExceptions.class, new Runnable() {
			public void run() {
				list.fire(new ICallback<ITestListener>() {
					public void process(ITestListener t) throws Exception {
						actual.add(t);
						Error e = new Error();
						exceptions.add(e);
						throw e;
					}
				});
			}
		});
		verify();
		assertEquals(exceptions, actualException.getCauses());
		assertEquals(Arrays.asList(mockL1, mockL2), actual);
	}

	public void testThreadDeathStopsListeners() {
		list.addListener(mockL1);
		list.addListener(mockL2);
		replay();
		final List<ITestListener> actual = Collections.synchronizedList(new ArrayList<ITestListener>());

		final ThreadDeath e = new ThreadDeath();
		ThreadDeath actualException = Tests.assertThrows(ThreadDeath.class, new Runnable() {
			public void run() {
				list.fire(new ICallback<ITestListener>() {
					public void process(ITestListener t) throws Exception {
						actual.add(t);
						throw e;
					}
				});
			}
		});
		verify();
		assertEquals(e, actualException);
		assertEquals(Arrays.asList(mockL1), actual);
	}

	public void testIfImplementsValidThenIsValidIsCalled() {
		list.addListener(mockV1);
		list.addListener(mockV2);

		EasyMock.expect(mockV1.isValid()).andReturn(true);
		EasyMock.expect(mockV2.isValid()).andReturn(true);
		replay();

		final List<ITestListener> actual = Collections.synchronizedList(new ArrayList<ITestListener>());
		list.fire(new ICallback<ITestListener>() {
			public void process(ITestListener listener) throws Exception {
				actual.add(listener);
			}
		});
		verify();
		assertEquals(Arrays.asList(mockV1, mockV2), actual);
	}

	public void testIfValidReturnsFalseThenListenerIsNotCalledAndRemovedFromList() {
		list.addListener(mockV1);
		list.addListener(mockV2);

		EasyMock.expect(mockV1.isValid()).andReturn(false);
		EasyMock.expect(mockV2.isValid()).andReturn(true);
		replay();

		final List<ITestListener> actual = Collections.synchronizedList(new ArrayList<ITestListener>());
		list.fire(new ICallback<ITestListener>() {
			public void process(ITestListener listener) throws Exception {
				actual.add(listener);
			}
		});
		verify();
		assertEquals(Arrays.asList(mockV2), actual);
	}

	private void replay() {
		EasyMock.replay(mockL1, mockL2, mockV1, mockV2, mockInvalid1, mockInvalid2);
	}

	private void verify() {
		EasyMock.verify(mockL1, mockL2, mockV1, mockV2, mockInvalid1, mockInvalid2);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rawList = new MultipleListenerList();
		list = (ListenerList<ITestListener>) IListenerList.Utils.<ITestListener> list(rawList, source);

		mockL1 = EasyMock.createMock(ITestListener.class);
		mockL2 = EasyMock.createMock(ITestListener.class);

		mockInvalid1 = EasyMock.createMock(ITestListenerAndValid.class);
		mockInvalid2 = EasyMock.createMock(ITestListenerAndValid.class);

		mockV1 = EasyMock.createMock(ITestListenerAndValid.class);
		mockV2 = EasyMock.createMock(ITestListenerAndValid.class);

		rawList.addListener(invalidSource, mockInvalid1);
		rawList.addListener(invalidSource, mockInvalid2);
	}
}
