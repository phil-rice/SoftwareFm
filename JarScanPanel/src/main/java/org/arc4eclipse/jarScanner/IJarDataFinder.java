package org.arc4eclipse.jarScanner;

import org.arc4eclipse.utilities.callbacks.ICallback;

public interface IJarDataFinder {

	void findDataAbout(String hexDigest, ICallback<IJarData> jarData);

}
