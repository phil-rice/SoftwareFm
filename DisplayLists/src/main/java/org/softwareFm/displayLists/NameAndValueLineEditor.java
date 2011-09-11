package org.softwareFm.displayLists;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.ILineEditable;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndTextField;
import org.softwareFm.utilities.collections.ICrud;

public class NameAndValueLineEditor extends AbstractLineEditor<NameAndValue, TitleAndTextField> {

	private final String nameKey;
	private final String valueKey;

	public NameAndValueLineEditor() {
		this(DisplayListsConstants.lineEditorNameKey, DisplayListsConstants.lineEditorValueKey);
	}

	protected NameAndValueLineEditor(String nameKey, String valueKey) {
		super(new NameAndValueCodec());
		this.nameKey = nameKey;
		this.valueKey = valueKey;
	}

	@Override
	public IHasControl makeLineControl(final ILineEditable<NameAndValue> lineEditable, Composite parent, final int index, NameAndValue t) {
		TitleAndTextField text = new TitleAndTextField(lineEditable.getDialogConfig(), parent, t.name, false);
		text.setText(t.url);
		addButtons(lineEditable, parent, index, text);
		return text;
	}

	@Override
	public void add(ILineEditable<NameAndValue> lineEditable) {
		ConfigForTitleAnd forDialogs = lineEditable.getDialogConfig();
		String nameTitle = Resources.getOrException(lineEditable.getResourceGetter(), nameKey);
		String valueTitle = Resources.getOrException(lineEditable.getResourceGetter(), valueKey);
		NameAndValueDialog dialog = new NameAndValueDialog(lineEditable.getShell(), SWT.NULL, forDialogs, nameTitle, valueTitle);
		NameAndValue result = dialog.open(new NameAndValue("", ""));
		if (result != null) {
			lineEditable.getModel().add(new NameAndValue(result.name, result.url));
			lineEditable.sendDataToServer();
		}
	}

	@Override
	public void edit(ILineEditable<NameAndValue> lineEditable, int index) {
		ConfigForTitleAnd forDialogs = lineEditable.getDialogConfig();
		String nameTitle = Resources.getOrException(lineEditable.getResourceGetter(), nameKey);
		String valueTitle = Resources.getOrException(lineEditable.getResourceGetter(), valueKey);
		NameAndValueDialog dialog = new NameAndValueDialog(lineEditable.getShell(), SWT.NULL, forDialogs, nameTitle, valueTitle);
		ICrud<NameAndValue> model = lineEditable.getModel();
		NameAndValue result = dialog.open(model.get(index));
		if (result != null) {
			model.set(index, result);
			lineEditable.sendDataToServer();
		}
	}

}
