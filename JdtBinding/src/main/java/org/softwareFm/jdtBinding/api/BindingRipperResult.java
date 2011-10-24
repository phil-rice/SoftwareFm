package org.softwareFm.jdtBinding.api;

import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.IBinding;
import org.softwareFm.utilities.exceptions.WrappedException;

/** this is my javadoc */
public class BindingRipperResult {

	public static final BindingRipperResult empty = new BindingRipperResult(null, null, null, null, null, null, null, null, null, null, null);
	public final IBinding binding;
	public final IPath path;
	public final IJavaElement javaElement;
	public final IJavaProject javaProject;
	public final String hexDigest;
	public final String packageName;
	public final String className;
	public final String methodName;
	public final IPackageFragmentRoot packageFragmentRoot;
	public final IPath sourceAttachmentPath;
	public final IClasspathEntry classpathEntry;
	public final Map<String, Object> cargo;

	public BindingRipperResult(IBinding binding, IJavaProject javaProject, IJavaElement javaElement, IPackageFragmentRoot packageFragment, IPath path, String hexDigest, IPath sourceAttachmentPath, String packageName, String className, String methodName, Map<String, Object> cargo) {
		super();
		try {
			this.binding = binding;
			if (cargo == null && binding != null)// allows tests where both are null..
				throw new NullPointerException();
			this.cargo = cargo;
			this.javaElement = javaElement;
			this.javaProject = javaProject;
			this.packageFragmentRoot = packageFragment;
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
		return "BindingRipperResult [path=" + path + "\npackageFragment=" + packageFragmentRoot + "\nHexDigest=" + hexDigest + "\nSrcAttachment = " + sourceAttachmentPath + "\nPackageName=" + packageName + "\nClassName=" + className + "\nMethodName=" + methodName + "]";
	}

}
