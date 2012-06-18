package org.softwarefm.softwarefm.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
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

	static class SfmActionAdapter extends Action implements IMenuCreator {
		private Menu menu;
		private final ISfmAction sfmAction;

		public SfmActionAdapter(ISfmAction sfmAction) {
			super("", SWT.NULL);
			this.sfmAction = sfmAction;
			setMenuCreator(this);
			setToolTipText(sfmAction.toolTip());
			setImageDescriptor(sfmAction.imageDescriptor());
		}

		@Override
		public void dispose() {
			if (menu != null)
				menu.dispose();
			menu = null;
		}

		@Override
		public Menu getMenu(Control parent) {
			if (menu != null)
				menu.dispose();
			menu = new Menu(parent);
			ActionContributionItem item1 = new ActionContributionItem(new Action("one") {
				@Override
				public void run() {
					sfmAction.execute();
				}
			});
			ActionContributionItem item2 = new ActionContributionItem(new Action("two") {
			});
			item1.fill(menu, -1);
			item2.fill(menu, -1);
			return menu;
		}

		@Override
		public Menu getMenu(Menu parent) {
			return getMenu(parent);
		}

	}

}
