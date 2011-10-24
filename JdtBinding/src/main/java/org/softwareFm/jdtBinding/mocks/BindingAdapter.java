package org.softwareFm.jdtBinding.mocks;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;

public class BindingAdapter implements IBinding {

	public IAnnotationBinding[] getAnnotations() {
		throw new UnsupportedOperationException();
	}

	public IJavaElement getJavaElement() {
		throw new UnsupportedOperationException();
	}

	public String getKey() {
		throw new UnsupportedOperationException();
	}

	public int getKind() {
		throw new UnsupportedOperationException();
	}

	public int getModifiers() {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}

	public boolean isDeprecated() {
		throw new UnsupportedOperationException();
	}

	public boolean isEqualTo(IBinding arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean isRecovered() {
		throw new UnsupportedOperationException();
	}

	public boolean isSynthetic() {
		throw new UnsupportedOperationException();
	}

}
