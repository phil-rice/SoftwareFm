package org.arc4eclipse.swtBasics;

import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Swts {

	public static void display(String title, IFunction1<Composite, Composite> builder) {
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setSize(300, 400);
			shell.setText(title);
			shell.setLayout(new FormLayout());
			Composite composite = builder.apply(shell);
			FormData fd = new FormData();
			fd.bottom = new FormAttachment(100, 0);
			fd.right = new FormAttachment(100, 0);
			fd.top = new FormAttachment(0, 0);
			fd.left = new FormAttachment(0, 0);
			composite.setLayoutData(fd);
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
