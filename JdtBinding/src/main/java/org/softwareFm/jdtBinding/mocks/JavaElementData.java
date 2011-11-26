/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jdtBinding.mocks;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;

public class JavaElementData {
	public final String name;
	public final Class<? extends IJavaElement> type;

	public IJavaElement makeJavaElement(Path path, JavaElementData parentData) {
		if (type == IMethod.class)
			return new MethodJavaElement(path, parentData, this);
		else if (type == IClassFile.class)
			return new ClassFileJavaElement(path, parentData, this);
		else if (type == ILocalVariable.class)
			return new LocalVariableJavaElement(path, parentData, this);
		throw new IllegalArgumentException(type.getName());
	}

	public JavaElementData(String name, Class<? extends IJavaElement> type) {
		this.name = name;
		this.type = type;
	}

	public String toString() {
		return "JavaElementData [name=" + name + ", type=" + type + "]";
	}

}