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

public class ExpressionCategoriser implements IExpressionCategoriser {

	@Override
	public ExpressionData categorise(Expression expression) {

		ITypeBinding binding = expression.resolveTypeBinding();
		if (expression instanceof SimpleName){
			ASTNode parent = expression.getParent();
			if (parent instanceof MethodInvocation){
				MethodInvocation methodInvocation = (MethodInvocation) parent;
				IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
				String methodKey = methodBinding.getKey();
				return new ExpressionData( methodKey);
			}
			if (parent instanceof MethodDeclaration){
				MethodDeclaration methodDeclaration = (MethodDeclaration) parent;
				IMethodBinding methodBinding = methodDeclaration.resolveBinding();
				String key = methodBinding.getKey();
				return new ExpressionData(key);
			}
			if (parent instanceof SimpleType){
				SimpleType simpleType = (SimpleType) parent;
				ITypeBinding parentBinding = simpleType.resolveBinding();
				String key = parentBinding.getKey();
				return new ExpressionData(key);
				
			}
			if (parent instanceof TypeDeclaration){
				String key = binding.getKey();
				return new ExpressionData(key);
			}
		}
		return null;
	}
}
