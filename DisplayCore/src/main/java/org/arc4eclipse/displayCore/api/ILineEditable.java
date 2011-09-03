package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.swtBasics.text.IButtonParent;
import org.arc4eclipse.utilities.collections.ICrud;
import org.eclipse.swt.widgets.Shell;

public interface ILineEditable<T> extends IButtonParent {

	Shell getShell();

	ICrud<T> getModel();

	DisplayerContext getDisplayerContext();

	ConfigForTitleAnd getDialogConfig();

	DisplayerDetails getDisplayerDetails();

	IRegisteredItems getRegisteredItems();

	void sendDataToServer();

}
