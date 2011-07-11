package org.arc4eclipse.shellPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class SoftwareFMShell extends org.eclipse.swt.widgets.Shell {

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			SoftwareFMShell shell = new SoftwareFMShell(display);
			ShellPanel shellPanel = new ShellPanel(shell, SWT.NULL);
			shellPanel.setBounds(0, 0, 1200, 800);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				try {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				} catch (Exception e) {
					e.printStackTrace();
					shellPanel.setException(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public SoftwareFMShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);

	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
