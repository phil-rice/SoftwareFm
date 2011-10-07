package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.editor.SoftwareFmIdEditor;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IEditorFactoryConfigurator;
import org.softwareFm.display.editor.StyledTextEditor;
import org.softwareFm.display.editor.TextEditor;

public class EditorFactoryConfigurator implements IEditorFactoryConfigurator{


	@Override
	public void process(IEditorFactory editorFactory) throws Exception {
		editorFactory.register("editor.text", new TextEditor());
		editorFactory.register("editor.styled.text", new StyledTextEditor());
		editorFactory.register("editor.softwareFm.id", new SoftwareFmIdEditor());
		
	}

}
