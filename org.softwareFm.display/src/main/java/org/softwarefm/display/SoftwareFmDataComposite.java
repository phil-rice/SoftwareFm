package org.softwarefm.display;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwarefm.display.displayer.IDisplayer;
import org.softwarefm.display.impl.DisplayerDefn;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.impl.SmallButtonDefn;

public class SoftwareFmDataComposite extends Composite {

	private final Composite topRow;

	public SoftwareFmDataComposite(Composite parent, SoftwareFmLayout layout, LargeButtonDefn... largeButtonDefns) {
		super(parent, SWT.NULL);
		topRow = new Composite(this, SWT.BORDER);
		topRow.setLayout(Swts.getHorizonalNoMarginRowLayout());
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			Composite smallButtonComposite = new Composite(topRow, SWT.BORDER);
			smallButtonComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
			smallButtonComposite.setLayoutData(new RowData(SWT.DEFAULT, layout.smallButtonCompositeHeight));
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				IHasControl hasControl = smallButtonDefn.smallButtonFactory.create(smallButtonComposite, smallButtonDefn, SWT.NULL);
				Control control = hasControl.getControl();
				control.setLayoutData(new RowData(layout.smallButtonWidth, layout.smallButtonHeight));
			}
		}
		for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			Composite largeButtonComposite = new Composite(this, SWT.BORDER) {
				@Override
				public String toString() {
					return largeButtonDefn.id + ":" + super.toString();
				}
			};
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				for (DisplayerDefn defn : smallButtonDefn.defns) {
					IDisplayer displayer = defn.displayer;
					IHasControl hasControl = displayer.create(largeButtonComposite, defn, SWT.NULL);
					Control control = hasControl.getControl();
				}
			}
			Swts.addGrabHorizontalAndFillGridDataToAllChildren(largeButtonComposite);
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
	}
}
