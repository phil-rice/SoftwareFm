package org.arc4eclipse.binding.mocks;

import org.eclipse.jdt.core.dom.IBinding;

public interface IBindingBuilder extends IBinding {

	IBindingBuilder withClazz(String className);

	IBindingBuilder child();

	IBindingBuilder withMethod(String string);

	IBindingBuilder withLocalVariable(String string);

	IBindingBuilder withInterface(String string);

	IBindingBuilder withPackage(String string);

	public class Utils {

		public static IBindingBuilder parent(String path) {
			return new BindingBuilder(path);

		}

	}

}
