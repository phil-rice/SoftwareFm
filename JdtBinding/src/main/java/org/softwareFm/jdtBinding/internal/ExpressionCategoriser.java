package org.softwareFm.jdtBinding.internal;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.softwareFm.jdtBinding.api.ExpressionData;
import org.softwareFm.jdtBinding.api.IExpressionCategoriser;
import org.softwareFm.utilities.strings.PreAndPost;
import org.softwareFm.utilities.strings.Strings;

public class ExpressionCategoriser implements IExpressionCategoriser {

	@Override
	public ExpressionData categorise(Expression expression) {
		if (expression != null)
			if (expression instanceof SimpleName) {
				ITypeBinding binding = expression.resolveTypeBinding();
				ASTNode parent = expression.getParent();
				if (parent instanceof MethodInvocation) {
					MethodInvocation methodInvocation = (MethodInvocation) parent;
					IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
					String methodKey = methodBinding.getKey();
					return makeExpressionData(methodKey);
				}
				if (parent instanceof MethodDeclaration) {
					MethodDeclaration methodDeclaration = (MethodDeclaration) parent;
					IMethodBinding methodBinding = methodDeclaration.resolveBinding();
					String key = methodBinding.getKey();
					return makeExpressionData(key);
				}
				if (parent instanceof SimpleType) {
					SimpleType simpleType = (SimpleType) parent;
					ITypeBinding parentBinding = simpleType.resolveBinding();
					String key = parentBinding.getKey();
					return makeExpressionData(key);

				}
				if (parent instanceof TypeDeclaration) {
					String key = binding.getKey();
					return makeExpressionData(key);
				}
			}
		return null;
	}

	private ExpressionData makeExpressionData(String key) {
		assert key.startsWith("L");
		PreAndPost classAndMethod = Strings.split(key, '.');
		String classKey = clean(classAndMethod.pre.substring(1), false);
		String methodKey = clean(classAndMethod.post, true);
		return new ExpressionData(classKey, methodKey);
	}
	
	private String clean(String string, boolean replaceSlash) {
		if (string == null)
			return null;
		String withoutGenerics = Strings.removeBrackets(string, '<','>');
		String cleaner = withoutGenerics.replace(";", "").replace("|", "_").replace("[", "(").replace('%', '_');
		String result = replaceSlash ? cleaner.replace("/", "_"): cleaner;
		return result;
	}

}
