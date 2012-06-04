package org.softwarefm.labelAndText;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.utilities.collections.Lists;

public class ButtonExecutorMock implements IButtonConfig {

	public final AtomicInteger canExecuteCount = new AtomicInteger();
	public final List<IGetTextWithKey> getTextWithKeys = Collections.synchronizedList(Lists.<IGetTextWithKey> newList());
	public boolean canExecute;
	public final AtomicInteger executeCount = new AtomicInteger();
	private final String key;

	public ButtonExecutorMock(String key) {
		this.key = key;
	}

	public boolean canExecute(IGetTextWithKey textWithKey) {
		canExecuteCount.incrementAndGet();
		getTextWithKeys.add(textWithKey);
		return canExecute;
	}

	public void execute() throws Exception {
		executeCount.incrementAndGet();
	}

	public String key() {
		return key;
	}

}
