package org.softwarefm.softwarefm.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.IActionBarConfigurator;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.softwarefm.SoftwareFmPlugin;
import org.softwarefm.softwarefm.action.EclipseActionBar;

abstract public class SoftwareFmView<C extends SoftwareFmComposite> extends ViewPart {
	abstract public C makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container);

	@Override
	public void createPartControl(Composite parent) {
		SoftwareFmPlugin activator = SoftwareFmPlugin.getDefault();
		SoftwareFmContainer<?> container = activator.getContainer();
		C panel = makeSoftwareFmComposite(parent, container);
		activator.addView(this, panel);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		configureToolbar(toolBarManager);
	}

	protected void configureToolbar(IToolBarManager toolBarManager) {
	}
	
	@Override
	public void dispose() {
		SoftwareFmPlugin activator = SoftwareFmPlugin.getDefault();
		activator.removeView(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	protected void configureToolbarWithProjectStuff(IToolBarManager toolBarManager) {
		SfmActionState actionState = SoftwareFmPlugin.getDefault().getActionState();
		SoftwareFmContainer<ITextSelection> container = SoftwareFmPlugin.getDefault().getContainer();
		IActionBarConfigurator configurator = IActionBarConfigurator.Utils.sfmConfigurator(container.resourceGetter, actionState, container.selectedBindingManager);
		EclipseActionBar eclipseActionBar = new EclipseActionBar(toolBarManager);
		configurator.configure(eclipseActionBar);
	}

}
