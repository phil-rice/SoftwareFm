package org.arc4eclipse.repositoryClient.api.impl;

import java.io.File;
import java.math.BigInteger;

import org.arc4eclipse.repositoryClient.api.IJarDetails;

public class JarDetails implements IJarDetails {

	private final File pathToJar;
	private final byte[] digest;
	private final String release;
	private String hexDigest;

	public JarDetails(File pathToJar, byte[] digest, String release) {
		super();
		this.pathToJar = pathToJar;
		this.digest = digest;
		this.release = release;
	}

	public String release() {
		return release;
	}

	public File pathToJar() {
		return pathToJar;
	}

	public byte[] digest() {
		return digest;
	}

	public String digestAsHexString() {
		if (hexDigest == null)
			if (digest == null)
				return "null";
			else
				hexDigest = new BigInteger(digest).toString(16);
		return hexDigest;
	}

	@Override
	public String toString() {
		return "JarDetails [pathToJar=" + pathToJar + ", release=" + release + ", digest=" + digestAsHexString() + "]";
	}
}
