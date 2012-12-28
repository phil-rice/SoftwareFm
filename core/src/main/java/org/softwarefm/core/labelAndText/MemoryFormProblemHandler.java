package org.softwarefm.core.labelAndText;

import java.util.List;

import org.softwarefm.utilities.collections.Lists;

public class MemoryFormProblemHandler implements IFormProblemHandler {

	public final List<List<KeyAndProblem>> problems = Lists.newList();
	public final List<List<String>> globalProblems = Lists.newList();
	public final List<ButtonComposite >buttonComposites = Lists.newList();

	public void handleAllProblems(ButtonComposite buttonComposite, List<KeyAndProblem> problems) {
		this.problems.add(problems);

	}

	public void handleGlobalProblems(ButtonComposite buttonComposite, List<String> globalProblems) {
		this.buttonComposites.add(buttonComposite);
		this.globalProblems.add(globalProblems);
	}

}
