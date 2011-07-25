package org.arc4eclipse.binding.mocks;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
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

	@Override
	public String[] getCategories() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IClassFile getClassFile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ICompilationUnit getCompilationUnit() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType getDeclaringType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFlags() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISourceRange getJavadocRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISourceRange getNameRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getOccurrenceCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType getType(String arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeRoot getTypeRoot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBinary() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISourceRange getSourceRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement[] getChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IAnnotation getAnnotation(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IAnnotation[] getAnnotations() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMemberValuePair getDefaultValue() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getExceptionTypes() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getNumberOfParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getParameterNames() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getParameterTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getRawParameterNames() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getReturnType() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSignature() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeParameter getTypeParameter(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getTypeParameterSignatures() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConstructor() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isMainMethod() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isResolved() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSimilar(IMethod arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ILocalVariable[] getParameters() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

}