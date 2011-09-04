package org.softwareFm.displayCore.api;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.displayCore.api.impl.DisplayContainerFactoryBuilder;
import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.utilities.functions.IFunction1;

public interface IDisplayContainerFactoryBuilder {

	/** Displayers are how we display stuff. For example we can register("text", new DisplayText()). Here after if you want to display "text" you use this displayer" */
	<L extends Control, C extends Control> void registerDisplayer(String displayerName, IDisplayer<L, C> displayer);

	/** A mapping from the name of an editor to the editor itself */
	void registerEditor(String editorName, IEditor editor);

	/** A mapping from the name of a line editor to the editor itself */
	void registerLineEditor(String lineEditorName, ILineEditor<?> editor);

	/** A mapping from the name of a validator to the validator itself */
	void registerValidator(String validatorName, IValidator validator);

	void registerImage(String name, IFunction1<Display, Image> imageMaker);

	void registerUrlGenerator(String name, IUrlGenerator generator);

	IDisplayContainerFactory build(String entity);

	public static class Utils {
		public static IDisplayContainerFactoryBuilder factoryBuilder() {
			return new DisplayContainerFactoryBuilder();
		}
	}

}
