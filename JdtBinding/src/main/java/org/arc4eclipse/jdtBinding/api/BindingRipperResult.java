package org.arc4eclipse.jdtBinding.api;

import org.eclipse.core.runtime.IPath;

/** this is my javadoc */
public class BindingRipperResult {

	public static final BindingRipperResult empty = new BindingRipperResult(null, null, null, null, null);
	public final IPath path;
	public final String hexDigest;
	public final String packageName;
	public final String className;
	public final String methodName;

	public BindingRipperResult(IPath path, String hexDigest, String packageName, String className, String methodName) {
		super();
		this.path = path;
		this.hexDigest = hexDigest;
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
	}

	@Override
	public String toString() {
		return "BindingRipperResult \nPath=" + path + "\nDigest: " + hexDigest + "\nPackageName=" + packageName + "\nClassName=" + className + "\nMethodName=" + methodName + "\n";
	}

}
