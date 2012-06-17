package org.softwarefm.softwarefm.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.IActionBarConfigurator;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.softwarefm.SoftwareFmActivator;
import org.softwarefm.softwarefm.action.EclipseActionBar;

abstract public class SoftwareFmView<C extends SoftwareFmComposite> extends ViewPart {
	abstract public C makePanel(Composite parent, SoftwareFmContainer<?> container);

	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmActivator activator = SoftwareFmActivator.getDefault();
		SoftwareFmContainer<?> container = activator.getContainer();
		makePanel(parent, container);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		configureToolbar(toolBarManager);

	}

	protected void configureToolbar(IToolBarManager toolBarManager) {
	}

	@Override
	public void setFocus() {
	}

	protected void configureToolbarWithProjectStuff(IToolBarManager toolBarManager) {
		SfmActionState actionState = SoftwareFmActivator.getDefault().getActionState();
		SoftwareFmContainer<ITextSelection> container = SoftwareFmActivator.getDefault().getContainer();
		IActionBarConfigurator configurator = IActionBarConfigurator.Utils.sfmConfigurator(container.resourceGetter, actionState, container.selectedBindingManager);
		EclipseActionBar eclipseActionBar = new EclipseActionBar(toolBarManager);
		configurator.configure(eclipseActionBar);
	}

}
