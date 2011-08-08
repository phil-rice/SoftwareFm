package org.arc4eclipse.jdtBinding.api;

import org.arc4eclipse.jdtBinding.api.FoundClassPathEntry.FoundIn;
import org.arc4eclipse.utilities.arrays.ArrayHelper;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class JavaProjects {

	public static FoundClassPathEntry findClassPathEntry(IJavaProject project, IClasspathEntry entry) {
		try {
			IClasspathEntry[] rawClasspaths = project.getRawClasspath();
			for (IClasspathEntry rawClassPath : rawClasspaths) {
				if (rawClassPath == entry)
					return new FoundClassPathEntry(project, FoundIn.FOUND_IN_RAW, entry);
				else if (rawClassPath.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
					IClasspathContainer container = JavaCore.getClasspathContainer(rawClassPath.getPath(), project);
					for (IClasspathEntry contained : container.getClasspathEntries()) {
						if (contained == entry)
							return new FoundClassPathEntry(project, FoundIn.FOUND_IN_LIBRARY, rawClassPath, container, entry);
					}
				}
			}
			return new FoundClassPathEntry(project, FoundIn.NOT_FOUND, entry);
		} catch (JavaModelException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void updateFoundClassPath(final FoundClassPathEntry found, final IFunction1<IClasspathEntry, IClasspathEntry> mutator) {
		try {
			switch (found.foundIn) {
			case FOUND_IN_LIBRARY:
				ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(found.container.getPath().segment(0));
				final IClasspathEntry[] newEntries = ArrayHelper.map(IClasspathEntry.class, found.container.getClasspathEntries(), new IFunction1<IClasspathEntry, IClasspathEntry>() {
					@Override
					public IClasspathEntry apply(IClasspathEntry from) throws Exception {
						if (from == found.classPathEntry)
							return mutator.apply(from);
						else
							return from;
					}
				});
				IClasspathContainer containerSuggestion = new IClasspathContainer() {

					@Override
					public IPath getPath() {
						return found.container.getPath();
					}

					@Override
					public int getKind() {
						return found.container.getKind();
					}

					@Override
					public String getDescription() {
						return found.container.getDescription();
					}

					@Override
					public IClasspathEntry[] getClasspathEntries() {
						return newEntries;
					}
				};

				initializer.requestClasspathContainerUpdate(found.container.getPath(), found.javaProject, containerSuggestion);
				break;
			default:
				throw new IllegalStateException(found.toString());
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}
