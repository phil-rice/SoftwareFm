package org.arc4eclipse.jarScanner;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.arc4eclipse.repositoryClient.api.impl.JarDetails;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.core.runtime.IPath;

public class JarScanner implements IJarScanner {

	@Override
	public void scan(IPath pathToJar, ICallback<JarDetails> callback) {
		try {
			File file = pathToJar.toFile();
			DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("SHA-1:"));
			byte[] buffer = new byte[8192];
			while (inputStream.read(buffer) != -1)
				;
			byte[] rawDigest = inputStream.getMessageDigest().digest();
			callback.process(new JarDetails(file, rawDigest, null));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}
