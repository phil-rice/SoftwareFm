package org.softwareFm.configuration.editor;

public interface ITester {

	String test();
	
	void processResults(String result);
	
	public static class Utils{
		public static String test(ITester tester){
			String result = tester.test();
			tester.processResults(result);
			return result;
		}
	}
	
}
