/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.softwareFm.crowdsource.utilities.arrays.ArrayHelper;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.eclipse.jdtBinding.FoundClassPathEntry.FoundIn;

public class JavaProjects {

	public static Iterable<IProject> allProjects() {
		return Iterables.iterable(ResourcesPlugin.getWorkspace().getRoot().getProjects());
	}

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
			case FOUND_IN_RAW: {
				final IClasspathEntry[] newEntries = cloneWithChange(found.javaProject.getRawClasspath(), found, mutator);
				found.javaProject.setRawClasspath(newEntries, new NullProgressMonitor());
				break;
			}
			case FOUND_IN_LIBRARY: {
				final IClasspathEntry[] newEntries = cloneWithChange(found.container.getClasspathEntries(), found, mutator);
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

				ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(found.container.getPath().segment(0));
				initializer.requestClasspathContainerUpdate(found.container.getPath(), found.javaProject, containerSuggestion);
				JavaCore.setClasspathContainer(found.container.getPath(), new IJavaProject[] { found.javaProject }, new IClasspathContainer[] { containerSuggestion }, new NullProgressMonitor());
				found.javaProject.getJavaModel().refreshExternalArchives(new IJavaElement[] { found.javaProject }, new NullProgressMonitor());

				break;
			}
			default:
				throw new IllegalStateException(found.toString());
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private static IClasspathEntry[] cloneWithChange(IClasspathEntry[] entriesToCopy, final FoundClassPathEntry found, final IFunction1<IClasspathEntry, IClasspathEntry> mutator) {
		final IClasspathEntry[] newEntries = ArrayHelper.map(IClasspathEntry.class, entriesToCopy, new IFunction1<IClasspathEntry, IClasspathEntry>() {
			@Override
			public IClasspathEntry apply(IClasspathEntry from) throws Exception {
				if (from != null && from == found.classPathEntry)
					return mutator.apply(from);
				else
					return from;
			}
		});
		return newEntries;
	}

	public static IJavadocSourceMutator sourceMutator(final IPackageFragmentRoot root, final IJavaProject javaProject, final IClasspathEntry classpathEntry) {
		return new IJavadocSourceMutator() {
			@Override
			public void setNewValue(String newValue, IJavadocSourceMutatorCallback whenComplete) {
				setSourceAttachment(javaProject, classpathEntry, newValue);
				String actual = findSourceFor(root);
				whenComplete.process(newValue, actual);
			}
		};

	}

	public static IJavadocSourceMutator javadocMutator(final IPackageFragmentRoot root, final IJavaProject javaProject, final IClasspathEntry classpathEntry, final Callable<String> findNewValue) {
		return new IJavadocSourceMutator() {
			@Override
			public void setNewValue(String newValue, IJavadocSourceMutatorCallback whenComplete) throws Exception {
				setJavadoc(javaProject, classpathEntry, newValue);
				String actual = findNewValue.call();
				whenComplete.process(newValue, actual);
			}
		};

	}

	public static void setSourceAttachment(IJavaProject javaProject, IClasspathEntry classpathEntry, final String newValue) {
		FoundClassPathEntry found = JavaProjects.findClassPathEntry(javaProject, classpathEntry);
		JavaProjects.updateFoundClassPath(found, new IFunction1<IClasspathEntry, IClasspathEntry>() {
			@Override
			public IClasspathEntry apply(IClasspathEntry from) throws Exception {
				IClasspathAttribute[] extraAttributes = from.getExtraAttributes();
				IPath newPath = newValue == null ? null : new Path(newValue);
				IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(from.getPath(), newPath, from.getSourceAttachmentRootPath(), from.getAccessRules(), extraAttributes, from.isExported());
				return newLibraryEntry;
			}
		});
	}

	public static void setJavadoc(IJavaProject javaProject, IClasspathEntry classpathEntry, final String newValue) {
		FoundClassPathEntry found = JavaProjects.findClassPathEntry(javaProject, classpathEntry);

		JavaProjects.updateFoundClassPath(found, new IFunction1<IClasspathEntry, IClasspathEntry>() {
			@Override
			public IClasspathEntry apply(IClasspathEntry from) throws Exception {
				IClasspathAttribute[] extraAttributes = from.getExtraAttributes();
				List<IClasspathAttribute> newAttributes = new ArrayList<IClasspathAttribute>();
				boolean found = false;
				IClasspathAttribute newClasspathAttribute = JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, newValue);
				for (IClasspathAttribute oldAttribute : extraAttributes)
					if (oldAttribute.getName().equals(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME)) {
						newAttributes.add(newClasspathAttribute);
						found = true;
					} else
						newAttributes.add(oldAttribute);
				if (newValue == null || newValue.equals(""))
					newAttributes.remove(newClasspathAttribute);
				else if (!found)
					newAttributes.add(newClasspathAttribute);
				IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(from.getPath(), from.getSourceAttachmentPath(), from.getSourceAttachmentRootPath(), from.getAccessRules(), newAttributes.toArray(new IClasspathAttribute[0]), from.isExported());
				return newLibraryEntry;
			}
		});
	}

	public static String findSourceFor(IPackageFragmentRoot root) {
		try {
			if (root != null) {
				IPath sourceAttachmentPath;
				sourceAttachmentPath = root.getSourceAttachmentPath();
				if (sourceAttachmentPath != null)
					return sourceAttachmentPath.toOSString();
			}
		} catch (JavaModelException e) {
		}
		return null;
	}

	public static String findJavadocFor(IClasspathEntry classpathEntry) {
		if (classpathEntry != null)
			for (IClasspathAttribute attribute : classpathEntry.getExtraAttributes())
				if (attribute.getName().equals(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME))
					return attribute.getValue();
		return null;
	}
}