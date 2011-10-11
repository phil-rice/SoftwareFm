package org.softwareFm.configuration.configurators;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.configuration.editor.JarEditor;
import org.softwareFm.configuration.editor.JavadocSourceEditor;
import org.softwareFm.configuration.editor.SoftwareFmIdEditor;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IEditorFactoryConfigurator;
import org.softwareFm.display.editor.StyledTextEditor;
import org.softwareFm.display.editor.TextEditor;

public class EditorFactoryConfigurator implements IEditorFactoryConfigurator{


	@Override
	public void process(IEditorFactory editorFactory) throws Exception {
		editorFactory.register(ConfigurationConstants.editorTextKey, new TextEditor());
		editorFactory.register(ConfigurationConstants.editorStyledTextKey, new StyledTextEditor());
		editorFactory.register(ConfigurationConstants.editorSfmIdKey, new SoftwareFmIdEditor());
		editorFactory.register("editor.javadoc", new JavadocSourceEditor(ConfigurationConstants.dataRawJavadoc, ConfigurationConstants.dataArtifactJavadoc, ConfigurationConstants.dataRawJavadocMutator));
		editorFactory.register("editor.source", new JavadocSourceEditor(ConfigurationConstants.dataRawSource, ConfigurationConstants.dataArtifactSource, ConfigurationConstants.dataRawSourceMutator));
		editorFactory.register("editor.jar", new JarEditor());
		
	}

}
