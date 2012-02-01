/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding.internal;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.softwareFm.common.strings.PreAndPost;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.jdtBinding.ExpressionData;
import org.softwareFm.eclipse.jdtBinding.IExpressionCategoriser;

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
		String withoutGenerics = Strings.removeBrackets(string, '<', '>');
		String cleaner = withoutGenerics.replace(";", "").replace("|", "_").replace("[", "(").replace('%', '_');
		String result = replaceSlash ? cleaner.replace("/", "_") : cleaner;
		return result;
	}

}