package org.arc4eclipse.jdtBinding.mocks;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

public class LocalVariableJavaElement extends JavaElementAdapter implements ILocalVariable {

	public LocalVariableJavaElement(Path path, JavaElementData parentData, JavaElementData javaElementData) {
		super(path, parentData, javaElementData);
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
	public IAnnotation getAnnotation(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IAnnotation[] getAnnotations() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISourceRange getNameRange() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTypeSignature() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMember getDeclaringMember() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFlags() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeRoot getTypeRoot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isParameter() {
		throw new UnsupportedOperationException();
	}
}
