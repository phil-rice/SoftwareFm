package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;

public interface IRegisteredItems {

	IValidator getValidator(String validatorName);

	IEditor getEditor(String editorName);

	ILineEditor getLineEditor(String lineEditorName);

	IDisplayer<?, ?> getDisplayer(String displayName);

	IUrlGenerator getUrlGenerator(String entityName);

}
