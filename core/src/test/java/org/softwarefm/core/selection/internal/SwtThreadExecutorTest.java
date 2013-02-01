package org.softwarefm.core.selection.internal;

import org.softwarefm.core.swt.Swts;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.callbacks.MemoryWithThreadCallback;
import org.softwarefm.utilities.events.IMultipleListenerListListener;
import org.softwarefm.utilities.events.internal.GlobalListenerContext;
import org.softwarefm.utilities.events.internal.GlobalListenerList;

public class SwtThreadExecutorTest extends SwtTest {

	private SwtThreadExecutor swtThreadExecutor;
	private static Object listener = "listener";
	private static Object source = "source";
	private static int eventId = 1;

	static class GlobalListenerMyMock implements IMultipleListenerListListener<Object> {

		int listenerStartedCount;
		int listenerEndedCount;

		@Override
		public <L> Object fireStarted(int eventId, Object source, Class<L> clazz, ICallback<L> callback) {
			return new Object();
		}

		@Override
		public void listenerStarted(int eventId, Object context, Object listener) {
			listenerStartedCount++;
			assertEquals(SwtThreadExecutorTest.listener, listener);
		}

		@Override
		public void listenerEnded(int eventId, Object context, Object listener) {
			listenerEndedCount++;
			assertEquals(listenerStartedCount, listenerEndedCount);
			assertEquals(SwtThreadExecutorTest.listener, listener);
		}

		@Override
		public <L> void fireEnded(int eventId, Object context) {
		}

	}

	public void testListenerIsCalledInDispatch() {
		MemoryWithThreadCallback<Object> memory = ICallback.Utils.<Object> memoryWithThread();
		GlobalListenerList globalList = new GlobalListenerList();
		GlobalListenerMyMock mock = new GlobalListenerMyMock();
		globalList.addGlobalListener(mock);

		GlobalListenerContext contexts = globalList.fireStart(eventId, source, Object.class, memory);
		swtThreadExecutor.execute(1, memory, listener, globalList, contexts);

		assertEquals(0, memory.getResults().size());
		assertEquals(0, mock.listenerStartedCount);
		assertEquals(0, mock.listenerEndedCount);
		
		Swts.dispatchUntilQueueEmpty(display);

		assertEquals(1, mock.listenerStartedCount);
		assertEquals(1, mock.listenerEndedCount);
		assertEquals(listener, memory.getOnlyResult());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		swtThreadExecutor = new SwtThreadExecutor(display);
	}
}
