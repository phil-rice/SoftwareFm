package org.softwarefm.softwarefm.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.softwarefm.eclipse.actions.IActionBar;
import org.softwarefm.eclipse.actions.ISfmAction;

public class EclipseActionBar implements IActionBar {
	private final IToolBarManager toolBarManager;

	public EclipseActionBar(IToolBarManager toolBarManager) {
		super();
		this.toolBarManager = toolBarManager;
	}

	@Override
	public void add(final ISfmAction sfmAction) {
		toolBarManager.add(new SfmActionAdapter(sfmAction));
	}

	static class SfmActionAdapter extends Action {
		private final ISfmAction sfmAction;

		public SfmActionAdapter(ISfmAction sfmAction) {
			super("", SWT.NULL);
			this.sfmAction = sfmAction;
			setToolTipText(sfmAction.toolTip());
			setImageDescriptor(sfmAction.imageDescriptor());
		}

		@Override
		public void run() {
			sfmAction.execute();
		}

	}

}