package org.softwareFm.utilities.events;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.utilities.callbacks.ICallback;

public class ListenerListTest extends TestCase {

	public void testAddRemoveAndFireListener() {
		ListenerList<ListenerMock> list = new ListenerList<ListenerMock>();
		ListenerMock mock1 = new ListenerMock(1);
		ListenerMock mock2 = new ListenerMock(2);
		list.add(mock1);
		send(list, "1");
		list.add(mock2);
		send(list, "1&2");
		list.remove(mock1);
		send(list, "2");
		list.remove(mock2);
		send(list, "-");
		assertEquals(Arrays.asList("1", "1&2"), mock1.messages);
		assertEquals(Arrays.asList("1&2", "2"), mock2.messages);
	}

	public void testAddRemoveListenerListListener() {
		ListenerList<ListenerMock> list = new ListenerList<ListenerMock>();
		list.add(new ListenerMock(1));
		list.add(new ListenerMock(2));
		send(list, "-");
		ListenerListListenerMock mock1 = new ListenerListListenerMock();
		ListenerListListenerMock mock2 = new ListenerListListenerMock();
		ListenerList.addListenerListListener(mock1);
		send(list, "1");
		ListenerList.addListenerListListener(mock2);
		send(list, "1&2");
		ListenerList.removeListenerListListener(mock1);
		send(list, "2");
		ListenerList.removeListenerListListener(mock2);
		send(list, "-");
		assertEquals(Arrays.asList("[L1, L2]: <1>", "[L1, L2]: <1&2>"), mock1.callbacks);
		assertEquals(Arrays.asList("[L1, L2]: <1&2>", "[L1, L2]: <2>"), mock2.callbacks);
	}

	private void send(ListenerList<ListenerMock> list, final String message) {
		list.fire(new ICallback<ListenerMock>() {
			@Override
			public void process(ListenerMock t) throws Exception {
				t.called(message);
			}

			@Override
			public String toString() {
				return "<" + message + ">";
			}
		});
	}
}
