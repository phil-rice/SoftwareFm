package org.softwarefm.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.softwarefm.eclipse.usage.UsageConstants;
import org.softwarefm.softwarefm.SoftwareFmPlugin;

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
