package org.softwareFm.common.callbacks;

public interface ICallbackWithFail<T> extends ICallback<T> {
	void fail();

}
