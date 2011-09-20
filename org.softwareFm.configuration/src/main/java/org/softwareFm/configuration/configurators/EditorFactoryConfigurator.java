package org.softwareFm.configuration.configurators;

import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IEditorFactoryConfigurator;
import org.softwareFm.display.editor.TextEditor;

public class EditorFactoryConfigurator implements IEditorFactoryConfigurator{


	@Override
	public void process(IEditorFactory editorFactory) throws Exception {
		editorFactory.register("editor.text", new TextEditor());
		
	}

}
