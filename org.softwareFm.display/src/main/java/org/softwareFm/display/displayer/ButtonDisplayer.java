package org.softwareFm.display.displayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.Actions;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAnd;
import org.softwareFm.utilities.strings.Strings;

public class ButtonDisplayer extends TitleAnd implements IDisplayer{

	protected final Button button;

	public ButtonDisplayer(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey) {
		super(config, parent, titleOrTitleKey, titleIsKey);
		button = new Button(getComposite(), SWT.PUSH);
		button.setLayoutData(new RowData(config.layout.valueWidth, config.layout.textHeight));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonPressed();
			};
		});
	}

	protected void buttonPressed() {
	}

	public void data(ActionContext actionContext, DisplayerDefn defn, String entity) {
		Object data = Actions.getValueFor(actionContext.dataGetter, defn);
		button.setText(Strings.nullSafeToString(data));
	}
}
