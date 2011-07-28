package org.arc4eclipse.displayTest;

import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Gash {

	public static void main(String[] args) {
		final IDisplayManager manager = IDisplayManager.Utils.displayManager();
		manager.registerDisplayer(IManyDisplayers.Utils.textDisplay());
		try {
			Display display = new Display();
			final Shell shell = new Shell(display);
			shell.setSize(300, 400);
			shell.setText("DisplayContainer");
			shell.setLayout(new GridLayout(1, false));
			final Composite composite = new Composite(shell, SWT.NULL);
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					Label label1 = makeLabel("hello");
					label1.dispose();
					makeLabel("world");
				}

				private Label makeLabel(String text) {
					Label label = new Label(composite, SWT.NULL);
					label.setText(text);
					label.setBounds(shell.getClientArea());
					return label;
				}
			});
			shell.pack();
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
