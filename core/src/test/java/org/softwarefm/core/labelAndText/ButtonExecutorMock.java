package org.softwarefm.core.labelAndText;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.core.labelAndText.IButtonConfig;
import org.softwarefm.core.labelAndText.IGetTextWithKey;
import org.softwarefm.core.labelAndText.KeyAndProblem;
import org.softwarefm.utilities.collections.Lists;

public class ButtonExecutorMock implements IButtonConfig {

	public final AtomicInteger canExecuteCount = new AtomicInteger();
	public final List<IGetTextWithKey> getTextWithKeys = Collections.synchronizedList(Lists.<IGetTextWithKey> newList());
	public List<KeyAndProblem> canExecute;
	public final AtomicInteger executeCount = new AtomicInteger();
	private final String key;

	public ButtonExecutorMock(String key, List<KeyAndProblem> canExecute) {
		this.canExecute = canExecute;
		this.key = key;
	}

	public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
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
