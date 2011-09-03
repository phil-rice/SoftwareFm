package org.softwareFm.jdtBinding.api;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.softwareFm.utilities.exceptions.WrappedException;

/** this is my javadoc */
public class BindingRipperResult {

	public static final BindingRipperResult empty = new BindingRipperResult(null, null, null, null, null, null, null, null, null);
	public final IPath path;
	public final IJavaElement javaElement;
	public final IJavaProject javaProject;
	public final String hexDigest;
	public final String packageName;
	public final String className;
	public final String methodName;
	public final IPackageFragmentRoot packageFragment;
	public final IPath sourceAttachmentPath;
	public final IClasspathEntry classpathEntry;

	public BindingRipperResult(IJavaProject javaProject, IJavaElement javaElement, IPackageFragmentRoot packageFragment, IPath path, String hexDigest, IPath sourceAttachmentPath, String packageName, String className, String methodName) {
		super();
		try {
			this.javaElement = javaElement;
			this.javaProject = javaProject;
			this.packageFragment = packageFragment;
			this.path = path;
			this.hexDigest = hexDigest;
			this.sourceAttachmentPath = sourceAttachmentPath;
			this.packageName = packageName;
			this.className = className;
			this.methodName = methodName;
			this.classpathEntry = packageFragment == null ? null : packageFragment.getResolvedClasspathEntry();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public String toString() {
		return "BindingRipperResult [path=" + path + "\npackageFragment=" + packageFragment + "\nHexDigest=" + hexDigest + "\nSrcAttachment = " + sourceAttachmentPath + "\nPackageName=" + packageName + "\nClassName=" + className + "\nMethodName=" + methodName + "]";
	}

}
