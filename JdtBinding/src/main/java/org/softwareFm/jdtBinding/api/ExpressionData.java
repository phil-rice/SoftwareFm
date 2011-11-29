package org.softwareFm.jdtBinding.api;

public class ExpressionData {

	public String classKey;
	public String methodKey;
	public ExpressionData(String classKey, String methodKey) {
		super();
		this.classKey = classKey;
		this.methodKey = methodKey;
	}
	@Override
	public String toString() {
		return "ExpressionData [classKey=" + classKey + ", methodKey=" + methodKey + "]";
	}


}
