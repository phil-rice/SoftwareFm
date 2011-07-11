package org.arc4eclipse.binding.mocks;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;

public class LocalVariableJavaElement extends JavaElementAdapter implements ILocalVariable {

	public LocalVariableJavaElement(Path path, JavaElementData parentData, JavaElementData javaElementData) {
		super(path, parentData, javaElementData);
	}

	
	public String getSource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public ISourceRange getSourceRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public IAnnotation getAnnotation(String arg0) {
		throw new UnsupportedOperationException();
	}

	
	public IAnnotation[] getAnnotations() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	
	public ISourceRange getNameRange() {
		throw new UnsupportedOperationException();
	}

	
	public String getTypeSignature() {
		throw new UnsupportedOperationException();
	}

}
