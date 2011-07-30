package org.arc4eclipse.swtBasics;

import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Swts {

	public static void addGrabHorizontalAndFillGridDataToAllChildren(Composite composite) {
		for (Control control : composite.getChildren()) {
			GridData data = makeGrabHorizonalAndFillGridData();
			control.setLayoutData(data);
		}
	}

	public static GridData makeGrabHorizonalAndFillGridData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		return data;
	}

	public static String layoutAsString(Control control) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(control.getClass().getSimpleName());
		Point size = control.getSize();
		buffer.append(" Size: " + size.x + ", " + size.y);
		if (control instanceof Composite)
			buffer.append(" Layout: " + ((Composite) control).getLayout());
		buffer.append(" LayoutData: " + control.getLayoutData());
		return buffer.toString();
	}

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
