package org.softwareFm.collections.explorer.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.collections.explorer.IAddCardCallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.runnable.Runnables;
import org.softwareFm.utilities.runnable.Runnables.CountRunnable;

public class AddCardCallbackMock implements IAddCardCallback {

	private final List<Map<String, Object>> canOkData = Lists.newList();
	private final CountRunnable ok = Runnables.count();
	private final CountRunnable cancel = Runnables.count();
	private boolean canOk;

	@Override
	public Runnable ok() {
		return ok;
	}

	@Override
	public Runnable cancel() {
		return cancel;
	}

	public void setCanOk(boolean canOk) {
		this.canOk = canOk;
	}

	@Override
	public boolean canOk(Map<String, Object> data) {
		canOkData.add(data);
		return canOk;
	}

}
