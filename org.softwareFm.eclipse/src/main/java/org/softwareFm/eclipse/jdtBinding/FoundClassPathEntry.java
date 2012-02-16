/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding;

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