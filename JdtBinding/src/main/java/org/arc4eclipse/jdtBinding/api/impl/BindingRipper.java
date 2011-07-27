package org.arc4eclipse.jdtBinding.api.impl;

import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.utilities.collections.Files;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class BindingRipper implements IBindingRipper {

	@Override
	public BindingRipperResult apply(IBinding from) {
		if (from != null) {
			IJavaElement javaElement = from.getJavaElement();
			if (javaElement != null) {
				IPath path = javaElement.getPath();
				String digestAsHexString = Files.digestAsHexString(path.toFile());
				if (javaElement instanceof IMethod) {
					String packageName = ((IMethodBinding) from).getDeclaringClass().getPackage().getName();
					String className = javaElement.getParent().getElementName();
					String methodName = javaElement.getElementName();
					return new BindingRipperResult(path, digestAsHexString, packageName, className, methodName);
				} else if (javaElement instanceof IClassFile) {
					String packageName = ((ITypeBinding) from).getPackage().getName();
					String className = javaElement.getElementName();
					return new BindingRipperResult(path, digestAsHexString, packageName, className, null);
				} else
					return new BindingRipperResult(path, digestAsHexString, "", "", "");
			}
		}
		return BindingRipperResult.empty;
	}

}
