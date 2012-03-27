package org.softwareFm.crowdsource.utilities.callbacks;

public interface ICallbackWithExceptionHandler <T> extends ICallback<T>{

	void handle(Exception e);
}
