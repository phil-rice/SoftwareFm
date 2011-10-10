package org.softwareFm.display.editor;

import java.util.Map;

public interface IEditorCompletion {

	void ok(Map<String, Object> value) ;

	void cancel();
	
	
	public static class Utils{
		public static IEditorCompletion sysout(final String title){
			return new IEditorCompletion() {
				
				@Override
				public void ok(Map<String, Object> value) {
					System.out.println("ok" + title + ": " + value);
				}
				
				@Override
				public void cancel() {
					System.out.println("cancel " + title);
				}
			};
		}
	}
}
