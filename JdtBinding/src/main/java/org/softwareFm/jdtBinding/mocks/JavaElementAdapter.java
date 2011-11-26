/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jdtBinding.mocks;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaModelException;

public class JavaElementAdapter implements IJavaElement {

	private final JavaElementData childData;
	private final IJavaElement parent;
	private final Path path;

	public JavaElementAdapter(Path path, JavaElementData parentData, JavaElementData childData) {
		this.parent = (parentData == null) ? null : parentData.makeJavaElement(path, null);
		this.childData = childData;
		this.path = path;
	}

	public IJavaElement getParent() {
		return parent;
	}

	public IPath getPath() {
		return path;
	}

	public String getElementName() {
		return childData.name;
	}

	public String toString() {
		return childData.name;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean exists() {
		throw new UnsupportedOperationException();
	}

	public IJavaElement getAncestor(int arg0) {
		throw new UnsupportedOperationException();
	}

	public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IResource getCorrespondingResource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public int getElementType() {
		throw new UnsupportedOperationException();
	}

	public String getHandleIdentifier() {
		throw new UnsupportedOperationException();
	}

	public IJavaModel getJavaModel() {
		throw new UnsupportedOperationException();
	}

	public IJavaProject getJavaProject() {
		throw new UnsupportedOperationException();
	}

	public IOpenable getOpenable() {
		throw new UnsupportedOperationException();
	}

	public IJavaElement getPrimaryElement() {
		throw new UnsupportedOperationException();
	}

	public IResource getResource() {
		throw new UnsupportedOperationException();
	}

	public ISchedulingRule getSchedulingRule() {
		throw new UnsupportedOperationException();
	}

	public IResource getUnderlyingResource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

	public boolean isStructureKnown() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

}