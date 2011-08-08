package org.arc4eclipse.jdtBinding.api;

import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

public class FoundClassPathEntry {

	public static enum FoundIn {
		NOT_FOUND, FOUND_IN_RAW, FOUND_IN_LIBRARY
	}

	public final IJavaProject javaProject;
	public final FoundIn foundIn;
	public final IClasspathEntry rawClassPath;
	public final IClasspathEntry classPathEntry;
	public final IClasspathContainer container;

	public FoundClassPathEntry(IJavaProject javaProject, FoundIn foundIn, IClasspathEntry classPathEntry) {
		this.javaProject = javaProject;
		this.foundIn = foundIn;
		this.rawClassPath = classPathEntry;
		this.container = null;
		this.classPathEntry = classPathEntry;
	}

	public FoundClassPathEntry(IJavaProject javaProject, FoundIn foundIn, IClasspathEntry rawClassPath, IClasspathContainer container, IClasspathEntry classPathEntry) {
		this.javaProject = javaProject;
		this.foundIn = foundIn;
		this.rawClassPath = rawClassPath;
		this.container = container;
		this.classPathEntry = classPathEntry;
	}

	@Override
	public String toString() {
		return "FoundClassPathEntry [project=" + javaProject.getElementName() + ", foundIn=" + foundIn + "]";
	}

}
