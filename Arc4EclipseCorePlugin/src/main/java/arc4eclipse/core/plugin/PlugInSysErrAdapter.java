package arc4eclipse.core.plugin;

public abstract class PlugInSysErrAdapter<T> implements IPlugInCreationCallback<T> {

	@Override
	public void onException(Throwable throwable) {
		throwable.printStackTrace();
	}

}
