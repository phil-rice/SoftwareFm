package org.arc4eclipse.jarScanner;

import org.arc4eclipse.repositoryClient.api.impl.JarDetails;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.eclipse.core.runtime.IPath;

public interface IJarScanner {

	void scan(IPath pathToJar, ICallback<JarDetails> callback);
}
