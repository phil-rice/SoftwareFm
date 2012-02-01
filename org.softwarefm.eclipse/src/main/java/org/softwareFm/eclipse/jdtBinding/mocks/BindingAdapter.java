/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding.mocks;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;

public class BindingAdapter implements IBinding {

	@Override
	public IAnnotationBinding[] getAnnotations() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement getJavaElement() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getKind() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getModifiers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDeprecated() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEqualTo(IBinding arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRecovered() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSynthetic() {
		throw new UnsupportedOperationException();
	}

}