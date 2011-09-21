package org.softwareFm.display.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;

public class TextDialog extends Dialog {

	private Shell shell;
	public String result;

	public TextDialog(Shell parent) {
		super(parent);
	}

	public String open(CompositeConfig compositeConfig, String title, String initialValue, ICallback<Object> onCompletion) {
		this.result = null;
		shell = new Shell(getParentShell(), SWT.NULL);
		createContents(shell, compositeConfig, title, initialValue, onCompletion);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(shell);
		shell.pack();
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	@Override
	public Shell getShell() {
		return shell;
	}

	private void createContents(Composite parent, CompositeConfig compositeConfig, String title, String initialValue, final ICallback<Object> onCompletion) {
		final TitleAndText txt = new TitleAndText(compositeConfig, parent, title, false);
		txt.setText(initialValue);
		Swts.makeAcceptCancelComposite(parent, SWT.NULL, compositeConfig.resourceGetter, new Runnable() {
			public void run() {
				try {
					result = txt.getText();
					onCompletion.process(txt.getText());
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		}, new Runnable() {
			public void run() {
				shell.close();
			}
		});
	}

	public static void main(String[] args) {

	}

}
