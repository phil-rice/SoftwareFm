package org.softwareFm.display.displayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.Actions;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.display.swt.Swts.Row;
import org.softwareFm.utilities.strings.Strings;

public abstract class ButtonDisplayer extends AbstractCompressed<Button> {

	public ButtonDisplayer(Composite parent, int style, CompositeConfig config) {
		super(parent, style, config);
	}

	@Override
	protected void setLayout() {
		content.setLayout(Grid.getGridLayoutWithoutMargins(2));
		buttonPanel.setLayout(Row.getHorizonalNoMarginRowLayout());
		control.setLayoutData(Grid.makeGrabHorizonalAndFillGridData());
	}

	@Override
	protected Button makeControl(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonPressed();
			};
		});
		return button;
	}

	abstract protected void buttonPressed();

	public void data(ActionContext actionContext, DisplayerDefn defn, String entity) {
		Object data = Actions.getValueFor(actionContext.dataGetter, defn);
		control.setText(Strings.nullSafeToString(data));
	}
}
