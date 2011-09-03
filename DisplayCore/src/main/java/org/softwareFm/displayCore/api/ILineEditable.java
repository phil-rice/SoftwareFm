package org.softwareFm.displayCore.api;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.collections.ICrud;

public interface ILineEditable<T> extends IButtonParent {

	Shell getShell();

	ICrud<T> getModel();

	DisplayerContext getDisplayerContext();

	ConfigForTitleAnd getDialogConfig();

	DisplayerDetails getDisplayerDetails();

	IRegisteredItems getRegisteredItems();

	void sendDataToServer();

}
