package org.softwareFm.jdtBinding.api;

import org.eclipse.jdt.core.dom.Expression;
import org.softwareFm.jdtBinding.internal.ExpressionCategoriser;

public interface IExpressionCategoriser {

	ExpressionData categorise(Expression expression);
	
	public static class Utils{
		public static IExpressionCategoriser categoriser(){
			return new ExpressionCategoriser();
		}
	}
}
