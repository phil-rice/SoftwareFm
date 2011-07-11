package org.arc4eclipse.binding.mocks;

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
