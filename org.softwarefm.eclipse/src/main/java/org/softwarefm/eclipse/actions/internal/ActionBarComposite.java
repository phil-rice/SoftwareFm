package org.softwarefm.eclipse.actions.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.IActionBar;
import org.softwarefm.eclipse.actions.ISfmAction;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.swt.HasComposite;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.resources.IResourceGetter;

public class ActionBarComposite extends HasComposite implements IActionBar {

	public ActionBarComposite(Composite parent) {
		super(parent);
		setLayout(Swts.Row.getHorizonalNoMarginRowLayout());
	}

	public void add(final ISfmAction sfmAction) {
		Label label = new Label(getComposite(), SWT.NULL);
		label.setImage(sfmAction.imageDescriptor().createImage());
		label.setToolTipText(sfmAction.toolTip());
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				sfmAction.execute();
			}
		});
	}

	public static void main(String[] args) {
		Swts.Show.display(ActionBarComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				IResourceGetter resourceGetter = SoftwareFmContainer.makeForTests(from.getDisplay()).resourceGetter;
				ActionBarComposite actionBarComposite = new ActionBarComposite(from);
				new SfmActionBarConfigurator(resourceGetter, new SfmActionState(), ISelectedBindingManager.Utils.noManager()).configure(actionBarComposite);
				return actionBarComposite.getComposite();
			}
		});
	}

}
