package org.softwarefm.labelAndText;

import java.util.List;

public interface IFormProblemHandler {

	/** The handler is notified about all the problems */
	void handleAllProblems(List<KeyAndProblem> problems);

	/** These are the problems that don't have a key (i.e. key is null) */
	void handleGlobalProblems(List<String> globalProblems);

	public static class Utils {
		public static IFormProblemHandler sysoutHandler() {
			return new IFormProblemHandler() {
				public void handleGlobalProblems(List<String> globalProblems) {
					System.out.println("Global: " + globalProblems);
				}

				public void handleAllProblems(List<KeyAndProblem> problems) {
					System.out.println("All: " + problems);
				}
			};
		}
	}
}
