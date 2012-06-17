package org.softwarefm.softwarefm.views;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.composite.ProjectComposite;

public class ProjectView extends SoftwareFmView<ProjectComposite> {

	@Override
	public ProjectComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ProjectComposite(parent, container);
	}

	@Override
	protected void configureToolbar(IToolBarManager toolBarManager) {
		configureToolbarWithProjectStuff(toolBarManager);
		IContributionItem comboCI = new ControlContribution("test") {
	        @Override
			protected Control createControl(Composite parent) {

	            final Combo c = new Combo(parent, SWT.READ_ONLY);
	            c.add("one");
	            c.add("two");
	            c.add("three");
	            c.addSelectionListener(new SelectionAdapter() {
	                 @Override
					public void widgetSelected(SelectionEvent e) {
	                     c.add("four");
	                  }
	                  });
	            return c;
	        }
	    };        

	    toolBarManager.add(comboCI);
	}

}
