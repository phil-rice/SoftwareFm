package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;

public interface IRegisteredItems {

	IValidator getValidator(String validatorName);

	IEditor getEditor(String editorName);

	<T> ILineEditor<T> getLineEditor(String lineEditorName);

	IDisplayer<?, ?> getDisplayer(String displayName);

	IUrlGenerator getUrlGenerator(String entityName);

}
