package org.softwarefm.eclipse.annotations.internal;

import org.eclipse.jdt.core.IJavaElement;
import org.softwarefm.eclipse.Jdts;
import org.softwarefm.eclipse.annotations.IJavaElementToUrl;
import org.softwarefm.utilities.functions.IFoldFunction;

public class JavaElementToUrl implements IJavaElementToUrl {

	@Override
	public String findUrl(IJavaElement javaElement) {
		String result = Jdts.foldParents(javaElement, "", new IFoldFunction<IJavaElement, String>() {
			@Override
			public String apply(IJavaElement element, String string) {
				String name = Jdts.visitJavaElement(element, Jdts.elementName());
				if (name == null)
					return string;
				else
					return name + "/" + string;
			}
		});
		return result;
	}

}
