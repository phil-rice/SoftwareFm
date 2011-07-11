package org.arc4eclipse.binding.path;

import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class JavaElementRipper implements IFunction1<IBinding, JavaElementRipperResult> {

	private final static JavaElementRipper instance = new JavaElementRipper();

	public static JavaElementRipperResult rip(IBinding binding) {
		return instance.apply(binding);
	}

	
	public JavaElementRipperResult apply(IBinding from) {
		if (from != null) {
			IJavaElement javaElement = from.getJavaElement();
			if (javaElement != null) {
				if (javaElement instanceof IMethod) {
					String packageName = ((IMethodBinding) from).getDeclaringClass().getPackage().getName();
					String className = javaElement.getParent().getElementName();
					String methodName = javaElement.getElementName();
					return new JavaElementRipperResult(javaElement.getPath(), packageName, className, methodName);
				} else if (javaElement instanceof IClassFile) {
					String packageName = ((ITypeBinding) from).getPackage().getName();
					String className = javaElement.getElementName();
					return new JavaElementRipperResult(javaElement.getPath(), packageName, className, null);
				}
			}
		}
		return JavaElementRipperResult.empty;
	}

}
