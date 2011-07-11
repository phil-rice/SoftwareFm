package org.arc4eclipse.binding.mocks;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

public class MethodJavaElement extends JavaElementAdapter implements IMethod {

	public MethodJavaElement(Path path, JavaElementData parentData, JavaElementData childData) {
		super(path, parentData, childData);
	}

	
	public String[] getCategories() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public IClassFile getClassFile() {
		throw new UnsupportedOperationException();
	}

	
	public ICompilationUnit getCompilationUnit() {
		throw new UnsupportedOperationException();
	}

	
	public IType getDeclaringType() {
		throw new UnsupportedOperationException();
	}

	
	public int getFlags() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public ISourceRange getJavadocRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public ISourceRange getNameRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public int getOccurrenceCount() {
		throw new UnsupportedOperationException();
	}

	
	public IType getType(String arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	
	public ITypeRoot getTypeRoot() {
		throw new UnsupportedOperationException();
	}

	
	public boolean isBinary() {
		throw new UnsupportedOperationException();
	}

	
	public String getSource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public ISourceRange getSourceRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public IJavaElement[] getChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public boolean hasChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public IAnnotation getAnnotation(String arg0) {
		throw new UnsupportedOperationException();
	}

	
	public IAnnotation[] getAnnotations() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public IMemberValuePair getDefaultValue() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public String[] getExceptionTypes() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public String getKey() {
		throw new UnsupportedOperationException();
	}

	
	public int getNumberOfParameters() {
		throw new UnsupportedOperationException();
	}

	
	public String[] getParameterNames() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public String[] getParameterTypes() {
		throw new UnsupportedOperationException();
	}

	
	public String[] getRawParameterNames() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public String getReturnType() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public String getSignature() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public ITypeParameter getTypeParameter(String arg0) {
		throw new UnsupportedOperationException();
	}

	
	public String[] getTypeParameterSignatures() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public boolean isConstructor() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public boolean isMainMethod() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public boolean isResolved() {
		throw new UnsupportedOperationException();
	}

	
	public boolean isSimilar(IMethod arg0) {
		throw new UnsupportedOperationException();
	}

}