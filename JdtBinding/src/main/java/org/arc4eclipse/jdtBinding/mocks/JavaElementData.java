package org.arc4eclipse.jdtBinding.mocks;

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
