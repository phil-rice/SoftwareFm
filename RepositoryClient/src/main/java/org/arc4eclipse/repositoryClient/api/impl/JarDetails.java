package org.arc4eclipse.repositoryClient.api.impl;

import org.arc4eclipse.repositoryClient.api.IJarDetails;

public class JarDetails implements IJarDetails {

	private final String pathToJar;
	private final String release;

	public JarDetails(String pathToJar, String release) {
		this.pathToJar = pathToJar;
		this.release = release;
	}

	public String release() {
		return release;
	}

	public String pathToJar() {
		return pathToJar;
	}

	
	public String toString() {
		return "JarDetails [pathToJar=" + pathToJar + ", release=" + release + "]";
	}

}
