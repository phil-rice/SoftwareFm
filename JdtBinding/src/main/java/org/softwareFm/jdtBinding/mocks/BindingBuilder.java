package org.softwareFm.jdtBinding.mocks;

import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IBinding;
import org.springframework.core.io.ClassPathResource;

public class BindingBuilder extends BindingAdapter implements IBindingBuilder, IBinding {

	protected final JavaElementData parentData;
	protected final JavaElementData childData;
	protected final boolean modifyingParent;
	protected final Path path;
	protected final String packageName;

	public BindingBuilder(String path, Class<?> markerClass) {
		this(makePathFrom(path, markerClass), "", true, new JavaElementData(path, null), new JavaElementData(path, null));
		System.out.println("Building: " + this.path);
	}

	private static Path makePathFrom(String path, Class<?> markerClass) {
		try {
			String canonicalPath = new ClassPathResource(path, markerClass).getFile().getCanonicalPath();
			return new Path(canonicalPath);
		} catch (IOException e) {
			return new Path(path);
		}
	}

	BindingBuilder(Path path, String packageName, boolean modifyingParent, JavaElementData parentData, JavaElementData childData) {
		this.parentData = parentData;
		this.childData = childData;
		this.modifyingParent = modifyingParent;
		this.path = path;
		this.packageName = packageName;
	}

	@Override
	public IBindingBuilder withPackage(String packageName) {
		return new BindingBuilder(path, packageName, modifyingParent, parentData, childData);
	}

	@Override
	public IBindingBuilder child() {
		return new BindingBuilder(path, packageName, false, parentData, childData);
	}

	@Override
	public IBindingBuilder withMethod(String methodName) {
		JavaElementData methodData = new JavaElementData(methodName, IMethod.class);
		return newMethodBuilder(methodData);
	}

	@Override
	public IBindingBuilder withClass(String className) {
		return withInterface(className);
	}

	@Override
	public IBindingBuilder withInterface(String className) {
		int dotIndex = className.lastIndexOf('.');
		String name = dotIndex == -1 ? className : className.substring(dotIndex + 1);
		Class<IClassFile> type = IClassFile.class;
		JavaElementData data = new JavaElementData(name, type);
		return newTypeBuilder(data);
	}

	protected IBindingBuilder newTypeBuilder(JavaElementData data) {
		if (modifyingParent)
			return new TypeBindingBuilder(path, packageName, modifyingParent, data, childData);
		else
			return new TypeBindingBuilder(path, packageName, modifyingParent, parentData, data);
	}

	protected IBindingBuilder newMethodBuilder(JavaElementData data) {
		if (modifyingParent)
			return new MethodBindingBuilder(path, packageName, modifyingParent, data, childData);
		else
			return new MethodBindingBuilder(path, packageName, modifyingParent, parentData, data);
	}

	protected IBindingBuilder newBuilder(JavaElementData data) {
		if (modifyingParent)
			return new BindingBuilder(path, packageName, modifyingParent, data, childData);
		else
			return new BindingBuilder(path, packageName, modifyingParent, parentData, data);
	}

	@Override
	public IBindingBuilder withLocalVariable(String string) {
		return newMethodBuilder(new JavaElementData(string, ILocalVariable.class));
	}

	@Override
	public IJavaElement getJavaElement() {
		return childData.makeJavaElement(path, parentData);
	}

	@Override
	public String toString() {
		return parentData.name + "/" + childData.name;
	}

}
