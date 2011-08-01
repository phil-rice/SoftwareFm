package org.arc4eclipse.utilities.events;

import java.util.List;

import org.arc4eclipse.utilities.collections.Lists;

public class ListenerMock {

	public final List<String> messages = Lists.newList();
	private final int num;

	public ListenerMock(int num) {
		this.num = num;
	}

	public void called(String message) {
		this.messages.add(message);
	}

	@Override
	public String toString() {
		return "L" + num;
	}

}
