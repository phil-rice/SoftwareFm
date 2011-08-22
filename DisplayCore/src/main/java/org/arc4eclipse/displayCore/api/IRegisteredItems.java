package org.arc4eclipse.displayCore.api;


public interface IRegisteredItems {

	IValidator getValidator(String validatorName);

	IEditor getEditor(String editorName);

	ILineEditor getLineEditor(String lineEditorName);

	IDisplayer<?, ?> getDisplayer(String displayName);

}
