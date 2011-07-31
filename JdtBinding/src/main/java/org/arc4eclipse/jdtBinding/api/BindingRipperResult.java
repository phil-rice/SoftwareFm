package org.arc4eclipse.jdtBinding.api;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/** this is my javadoc */
public class BindingRipperResult {

	public static final BindingRipperResult empty = new BindingRipperResult(null, null, null, null, null, null, null);
	public final IPath path;
	public final IJavaProject javaProject;
	public final String hexDigest;
	public final String packageName;
	public final String className;
	public final String methodName;
	public final IPackageFragmentRoot packageFragment;

	public BindingRipperResult(IJavaProject javaProject, IPackageFragmentRoot packageFragment, IPath path, String hexDigest, String packageName, String className, String methodName) {
		super();
		this.javaProject = javaProject;
		this.packageFragment = packageFragment;
		this.path = path;
		this.hexDigest = hexDigest;
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
	}

	@Override
	public String toString() {
		return "BindingRipperResult [path=" + path + "\nHexDigest=" + hexDigest + "\nPackageName=" + packageName + "\nClassName=" + className + "\nMethodName=" + methodName + "]";
	}

}
