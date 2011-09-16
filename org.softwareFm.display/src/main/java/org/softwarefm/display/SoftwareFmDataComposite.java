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
import org.eclipse.swt.widgets.Group;
import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwareFm.swtBasics.IHasComposite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.displayer.IDisplayerFactory;
import org.softwarefm.display.impl.DisplayerDefn;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.ImageButtonConfig;

public class SoftwareFmDataComposite implements IHasComposite {

	private final Composite content;
	private final Composite topRow;
	private final DisplaySelectionModel displaySelectionModel;
	private final Map<String, IControlWithToggle> smallButtonIdToControlWithToggleMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, List<IHasControl>> smallButtonIdToLargeHasControlMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, Group> smallButtonIdToGroupMap = Maps.newMap(LinkedHashMap.class);

	public SoftwareFmDataComposite(final Composite parent, CompositeConfig compositeConfig, ICallback<Throwable> exceptionHandler, LargeButtonDefn... largeButtonDefns) {
		this.content = new Composite(parent, SWT.NULL);
		IResourceGetter resourceGetter = compositeConfig.resourceGetter;
		displaySelectionModel = new DisplaySelectionModel(exceptionHandler, largeButtonDefns);
		topRow = new Composite(content, SWT.BORDER);
		topRow.setLayout(Swts.getHorizonalNoMarginRowLayout());
		ImageButtonConfig imageButtonConfig = compositeConfig.imageButtonConfig;
		SoftwareFmLayout layout = imageButtonConfig.layout;
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			Composite smallButtonComposite = new Composite(topRow, SWT.BORDER);
			smallButtonComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
			smallButtonComposite.setLayoutData(new RowData(SWT.DEFAULT, layout.smallButtonCompositeHeight));
			for (final SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				IControlWithToggle hasControl = smallButtonDefn.smallButtonFactory.create(smallButtonComposite, smallButtonDefn, imageButtonConfig.withImage(smallButtonDefn.mainImageId));
				smallButtonIdToControlWithToggleMap.put(smallButtonDefn.id, hasControl);
				Control control = hasControl.getControl();
				control.setLayoutData(new RowData(layout.smallButtonWidth, layout.smallButtonHeight));
				control.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						displaySelectionModel.select(smallButtonDefn.id);
						System.out.println("Selected: " + smallButtonDefn.id);
						Swts.layoutDump(parent);
					}
				});

			}
		}
		for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				Group group = new Group(content, SWT.SHADOW_ETCHED_IN);
				group.setVisible(false);
				smallButtonIdToGroupMap.put(smallButtonDefn.id, group);
				group.setText(Resources.getOrException(resourceGetter, smallButtonDefn.titleId));
				for (DisplayerDefn defn : smallButtonDefn.defns) {
					IDisplayerFactory displayer = defn.displayer;
					IHasControl hasControl = displayer.create(group, defn, SWT.NULL, compositeConfig);
					Maps.addToList(smallButtonIdToLargeHasControlMap, smallButtonDefn.id, hasControl);
				}
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(group);
			}
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
		updateVisibleData();
		displaySelectionModel.addDisplaySelectionListener(new IDisplaySelectionListener() {
			@Override
			public void smallButtonPressed(DisplaySelectionModel model, LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn) {
				updateVisibleData();
				boolean visible = displaySelectionModel.getVisibleSmallButtonsId().contains(smallButtonDefn.id);
				IControlWithToggle control = smallButtonIdToControlWithToggleMap.get(smallButtonDefn.id);
				control.setValue(!visible);
			}
		});
	}

	private void updateVisibleData() {
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				final List<String> visible = displaySelectionModel.getVisibleSmallButtonsId();
				Swts.sortVisibilityForComposites(smallButtonIdToGroupMap, new IFunction1<String, Boolean>() {
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
