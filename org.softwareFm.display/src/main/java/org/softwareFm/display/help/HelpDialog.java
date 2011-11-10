package org.softwareFm.display.help;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.utilities.functions.IFunction1;

public class HelpDialog extends Composite {

	public HelpDialog(Composite parent, int style) {
		super(parent, style);
		Button help = new Button(this, SWT.PUSH);
		help.setText("Help");
		Grid.addGrabHorizontalAndFillGridDataToAllChildren(this);
		help.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openQuestion(getShell(), "Title", "message");
			}
		});

	}

	public static void main(String[] args) {
		Show.display("Help Dialog", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new HelpDialog(from, SWT.NULL);
			}
		});
	}

}
