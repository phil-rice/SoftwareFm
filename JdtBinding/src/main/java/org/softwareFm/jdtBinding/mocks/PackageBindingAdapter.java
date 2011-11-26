/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jdtBinding.mocks;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;

class PackageBindingAdapter implements IPackageBinding {

	public boolean isSynthetic() {
		throw new UnsupportedOperationException();
	}

	public boolean isRecovered() {
		throw new UnsupportedOperationException();
	}

	public boolean isEqualTo(IBinding arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean isDeprecated() {
		throw new UnsupportedOperationException();
	}

	public int getModifiers() {
		throw new UnsupportedOperationException();
	}

	public int getKind() {
		throw new UnsupportedOperationException();
	}

	public String getKey() {
		throw new UnsupportedOperationException();
	}

	public IJavaElement getJavaElement() {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding[] getAnnotations() {
		throw new UnsupportedOperationException();
	}

	public boolean isUnnamed() {
		throw new UnsupportedOperationException();
	}

	public String[] getNameComponents() {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}
}