package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.displayCore.api.impl.DisplayContainerFactoryBuilder;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

public interface IDisplayContainerFactoryBuilder {
	<L extends Control, C extends Control> void registerDisplayer(String displayerName, IDisplayer<L, C> displayer);

	void registerEditor(String editorName, IEditor editor);

	void registerLineEditor(String lineEditorName, ILineEditor editor);

	void registerValidator(String validatorName, IValidator validator);

	void registerEntity(String string);

	void registerForEntity(String entity, String key, String title, String help);

	void registerForEntity(String entity, String key, String title, String help, IFunction1<Device, Image> imageMaker);

	IDisplayContainerFactory build(String entityName);

	public static class Utils {
		public static IDisplayContainerFactoryBuilder factoryBuilder() {
			return new DisplayContainerFactoryBuilder();
		}
	}

}
