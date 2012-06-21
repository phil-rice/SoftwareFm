package org.softwarefm.labelAndText;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.strings.Strings;

public interface IFormProblemHandler {

	/** The handler is notified about all the problems 
	 * @param buttonComposite TODO*/
	void handleAllProblems(ButtonComposite buttonComposite, List<KeyAndProblem> problems);

	/** These are the problems that don't have a key (i.e. key is null) */
	void handleGlobalProblems(ButtonComposite buttonComposite, List<String> globalProblems);

	public static class Utils {
		public static MemoryFormProblemHandler memoryHandler() {
			return new MemoryFormProblemHandler();
		}

		/** global problems are reported in this buttons tooltip */
		public static IFormProblemHandler buttonTooltipProblemHandler(final String buttonKey) {
			return new IFormProblemHandler() {
				public void handleGlobalProblems(ButtonComposite buttonComposite, List<String> globalProblems) {
				}

				public void handleAllProblems(ButtonComposite buttonComposite, List<KeyAndProblem> problems) {
					String problemText = Strings.join(Iterables.map(problems, new IFunction1<KeyAndProblem, String>() {
						public String apply(KeyAndProblem from) throws Exception {
							return from.problem;
						}
					}), "\n");
					Button button = (Button) buttonComposite.getButton(buttonKey);
					button.setToolTipText(problemText);
					boolean problem = problems.size() > 0;
					Color color = button.getDisplay().getSystemColor(problem ? SWT.COLOR_RED : SWT.COLOR_WIDGET_BACKGROUND);
					button.setBackground(color);
					button.setEnabled(problem);
				}
			};

		}

		public static IFormProblemHandler noHandler() {
			return new IFormProblemHandler() {
				public void handleGlobalProblems(ButtonComposite buttonComposite, List<String> globalProblems) {
				}

				public void handleAllProblems(ButtonComposite buttonComposite, List<KeyAndProblem> problems) {
				}
			};
		}

		public static IFormProblemHandler sysoutHandler() {
			return new IFormProblemHandler() {
				public void handleGlobalProblems(ButtonComposite buttonComposite, List<String> globalProblems) {
					System.out.println("Global: " + globalProblems);
				}

				public void handleAllProblems(ButtonComposite buttonComposite, List<KeyAndProblem> problems) {
					System.out.println("All: " + problems);
				}
			};
		}
	}
}
