/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding;

import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;

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
	public final Expression expression;

	public BindingRipperResult(Expression expression, IJavaProject javaProject, IJavaElement javaElement, IPackageFragmentRoot packageFragment, IPath path, String hexDigest, IPath sourceAttachmentPath, String packageName, String className, String methodName, Map<String, Object> cargo) {
		super();
		try {
			this.expression = expression;
			this.binding = expression == null ? null : expression.resolveTypeBinding();
			if (cargo == null && expression != null)// allows tests where both are null..
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
		return "BindingRipperResult [\npackageName=" + packageName + "\nclassName=" + className + "\nmethodName=" + methodName + "\npath=" + path + "\nhexDigest=" + hexDigest + "\nsourceAttachmentPath=" + Strings.oneLine(sourceAttachmentPath) + "\nclasspathEntry=" + classpathEntry + "\ncargo=" + cargo + "\nexpression=" + expression + "]";
	}

}