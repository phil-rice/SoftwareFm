package org.softwarefm.display;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swtBasics.IHasComposite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwarefm.display.displayer.IDisplayer;
import org.softwarefm.display.impl.DisplayerDefn;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ImageButtonConfig;

public class SoftwareFmDataComposite implements IHasComposite {

	private final Composite content;
	private final Composite topRow;
	private final DisplaySelectionModel displaySelectionModel;
	private final Map<String, List<IHasControl>> smallButtonIdToHasControlMap = Maps.newMap(LinkedHashMap.class);

	public SoftwareFmDataComposite(Composite parent, ImageButtonConfig imageButtonConfig, ICallback<Throwable> exceptionHandler, LargeButtonDefn...largeButtonDefns) {
		this.content = new Composite(parent, SWT.NULL);
		displaySelectionModel = new DisplaySelectionModel(exceptionHandler, largeButtonDefns);
		topRow = new Composite(content, SWT.BORDER);
		topRow.setLayout(Swts.getHorizonalNoMarginRowLayout());
		SoftwareFmLayout layout = imageButtonConfig.layout;
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			Composite smallButtonComposite = new Composite(topRow, SWT.BORDER);
			smallButtonComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
			smallButtonComposite.setLayoutData(new RowData(SWT.DEFAULT, layout.smallButtonCompositeHeight));
			for (final SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				IHasControl hasControl = smallButtonDefn.smallButtonFactory.create(smallButtonComposite, smallButtonDefn, imageButtonConfig.withImage(smallButtonDefn.mainImageId));
				Control control = hasControl.getControl();
				control.setLayoutData(new RowData(layout.smallButtonWidth, layout.smallButtonHeight));
				control.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					displaySelectionModel.select(smallButtonDefn.id);
					System.out.println("Selected: " + smallButtonDefn.id);
				}});
			}
		}
		for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				for (DisplayerDefn defn : smallButtonDefn.defns) {
					IDisplayer displayer = defn.displayer;
					IHasControl hasControl = displayer.create(content, defn, SWT.NULL);
					Maps.addToList(smallButtonIdToHasControlMap,smallButtonDefn.id, hasControl);
				}
			}
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
		updateVisibleData();
		displaySelectionModel.addDisplaySelectionListener(new IDisplaySelectionListener() {
			@Override
			public void smallButtonPressed(DisplaySelectionModel model, LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn) {
				updateVisibleData();
			}
		});
	}


	private void updateVisibleData() {
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				final List<String> visible = displaySelectionModel.getVisibleSmallButtonsId();
				Swts.sortVisibilityForHasControlList(smallButtonIdToHasControlMap, new IFunction1<String,Boolean>() {
					@Override
					public Boolean apply(String from) throws Exception {
						return visible.contains(from);
					}
				});
				content.layout();
				content.getParent().layout();
				content.getParent().redraw();
			}

		});
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}
}
