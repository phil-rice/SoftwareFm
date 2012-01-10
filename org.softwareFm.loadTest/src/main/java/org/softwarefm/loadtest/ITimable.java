package org.softwarefm.loadtest;

public interface ITimable<Context> {
	Context start(int thread);

	void execute(Context context, int thread, int index) throws Exception;

	void finished(Context context, int thread);

}
