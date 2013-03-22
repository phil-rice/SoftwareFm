package org.softwarefm.eclipse.annotations;

import org.eclipse.jdt.core.IJavaElement;
import org.softwarefm.eclipse.annotations.internal.JavaElementToUrl;

public interface IJavaElementToUrl {

	String findUrl(IJavaElement javaElement);

	public static class Utils {

		private static IJavaElementToUrl instance = new JavaElementToUrl();

		public static IJavaElementToUrl javaElementToUrl() {
			return instance;
		}
	}
}
