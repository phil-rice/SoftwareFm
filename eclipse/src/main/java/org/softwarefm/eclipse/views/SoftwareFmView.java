package org.softwarefm.eclipse.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.actions.IActionBarConfigurator;
import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.composite.SoftwareFmComposite;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.eclipse.action.EclipseActionBar;

abstract public class SoftwareFmView<C extends SoftwareFmComposite> extends ViewPart {
	private C panel;

	abstract public C makeSoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container);

	@Override
	public void createPartControl(Composite parent) {
		System.out.println("createPartControl: " + getClass().getSimpleName());
		SoftwareFmPlugin activator = SoftwareFmPlugin.getDefault();
		SoftwareFmContainer<?> container = activator.getContainer();
		panel = makeSoftwareFmComposite(parent, container);
		activator.addView(this, panel);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		configureToolbar(toolBarManager);
		System.out.println("end of createPartControl: " + getClass().getSimpleName());
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
		panel.getComposite().setFocus();
	}

	protected void configureToolbarWithProjectStuff(IToolBarManager toolBarManager) {
		SfmActionState actionState = SoftwareFmPlugin.getDefault().getActionState();
		SoftwareFmContainer<ITextSelection> container = SoftwareFmPlugin.getDefault().getContainer();
		IActionBarConfigurator configurator = IActionBarConfigurator.Utils.sfmConfigurator(container.resourceGetter, actionState, container.selectedBindingManager);
		EclipseActionBar eclipseActionBar = new EclipseActionBar(toolBarManager);
		configurator.configure(eclipseActionBar);
	}

}
