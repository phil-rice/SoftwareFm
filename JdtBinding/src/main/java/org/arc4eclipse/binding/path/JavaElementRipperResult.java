package org.arc4eclipse.binding.path;

import org.eclipse.core.runtime.IPath;

/** this is my javadoc */
public class JavaElementRipperResult {

	public static final JavaElementRipperResult empty = new JavaElementRipperResult(null, null, null, null);
	public final IPath path;
	public final String packageName;
	public final String className;
	public final String methodName;

	public JavaElementRipperResult(IPath path, String packageName, String className, String methodName) {
		this.path = path;
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
	}

	
	public String toString() {
		return "JavaElementRipperResult [path=" + path + ", packageName=" + packageName + ", className=" + className + ", methodName=" + methodName + "]";
	}

}
