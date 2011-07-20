package org.arc4eclipse.panelExerciser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.arc4eclipse.panelExerciser.constants.PanelExerciserConstants;
import org.arc4eclipse.panelExerciser.fixtures.AllTestFixtures;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class BindingList extends org.eclipse.swt.widgets.Composite {
	private final Logger logger = Logger.getLogger(getClass());
	private Tree tree1;

	/**
	 * Auto-generated main method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void main(String[] args) {
		showGUI();

	}

	/**
	 * Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	 */

	@Override
	protected void checkSubclass() {
	}

	private final List<IBindingSelectedListener> listenerList = new ArrayList<IBindingSelectedListener>();

	public void addFireSelectionListener(IBindingSelectedListener listener) {
		listenerList.add(listener);
	}

	public void removeFireSelectionListener(IBindingSelectedListener listener) {
		listenerList.remove(listener);
	}

	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static BindingList showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		BindingList inst = new BindingList(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if (size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		inst.setData(AllTestFixtures.allConstants(IBinding.class));
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return inst;
	}

	public BindingList(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			this.setSize(298, 213);
			{
				FormData tree1LData = new FormData();
				tree1LData.left = new FormAttachment(0, 1000, 0);
				tree1LData.top = new FormAttachment(0, 1000, -3);
				tree1LData.width = 281;
				tree1LData.height = 196;
				tree1LData.bottom = new FormAttachment(1000, 1000, -3);
				tree1LData.right = new FormAttachment(1000, 1000, 0);
				tree1 = new Tree(this, SWT.NONE);
				tree1.setLayoutData(tree1LData);
				tree1.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent evt) {
						Object item = evt.item.getData();
						logger.debug(MessageFormat.format(PanelExerciserConstants.selected, item));
						IBinding file = item instanceof IBinding ? (IBinding) item : null;
						for (IBindingSelectedListener listener : listenerList)
							listener.bindingSelected(file);
					}
				});
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setData(Object data) {
		super.setData(data);
		if (data instanceof Iterable) {
			for (Object item : (Iterable) data) {
				if (item instanceof IBinding) {
					logger.debug(MessageFormat.format(PanelExerciserConstants.setDataInBindingList, item));
					TreeItem treeItem = new TreeItem(tree1, SWT.NULL);
					treeItem.setText(item.toString());
					treeItem.setData(item);
				}
			}
			return;
		}
		tree1.removeAll();
	}
}
