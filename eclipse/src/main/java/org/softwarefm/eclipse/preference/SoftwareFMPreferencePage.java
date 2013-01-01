package org.softwarefm.eclipse.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.shared.usage.UsageConstants;

public class SoftwareFMPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(SoftwareFmPlugin.getDefault().getPreferenceStore());
		setDescription("");
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(UsageConstants.recordUsageKey, "&Record Usage", getFieldEditorParent()));
	}


}
