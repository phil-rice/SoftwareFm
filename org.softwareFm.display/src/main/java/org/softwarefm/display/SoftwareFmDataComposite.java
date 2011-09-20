package org.softwareFm.display;

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
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.DataGetter;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.data.IGuiDataListener;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SmallButtonDefn;
import org.softwareFm.swtBasics.IHasComposite;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class SoftwareFmDataComposite implements IHasComposite {

	private final Composite content;
	private final Composite topRow;
	private final DisplaySelectionModel displaySelectionModel;
	private final Map<String, ISmallDisplayer> smallButtonIdToSmallDisplayerMap = Maps.newMap(LinkedHashMap.class);
	private final Map<DisplayerDefn, IDisplayer> displayDefnToDisplayerMap = Maps.newMap(LinkedHashMap.class);
	// private final Map<String, List<IDisplayer>> smallButtonIdToDisplayerMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, Group> smallButtonIdToGroupMap = Maps.newMap(LinkedHashMap.class);

	public SoftwareFmDataComposite(final Composite parent, GuiDataStore guiDataStore, CompositeConfig compositeConfig, final ActionStore actionStore, IEditorFactory editorFactory, ICallback<Throwable> exceptionHandler, final LargeButtonDefn... largeButtonDefns) {
		this.content = new Composite(parent, SWT.NULL);
		final IResourceGetter resourceGetter = compositeConfig.resourceGetter;
		displaySelectionModel = new DisplaySelectionModel(exceptionHandler, largeButtonDefns);
		topRow = new Composite(content, SWT.BORDER);
		topRow.setLayout(Swts.getHorizonalNoMarginRowLayout());
		ImageButtonConfig imageButtonConfig = compositeConfig.imageButtonConfig;
		SoftwareFmLayout layout = imageButtonConfig.layout;
		final IDataGetter dataGetter = new DataGetter(guiDataStore, compositeConfig.resourceGetter);
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			SimpleButtonParent smallButtonComposite = new SimpleButtonParent(topRow, layout, SWT.BORDER);
			smallButtonComposite.getButtonComposite().setLayoutData(new RowData(SWT.DEFAULT, layout.smallButtonCompositeHeight));
			for (final SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				ISmallDisplayer smallDisplayer = smallButtonDefn.smallButtonFactory.create(smallButtonComposite, smallButtonDefn, imageButtonConfig.withImage(smallButtonDefn.mainImageId));
				smallButtonIdToSmallDisplayerMap.put(smallButtonDefn.id, smallDisplayer);
				Control control = smallDisplayer.getControl();
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
		final ActionContext actionContext = new ActionContext(new DataGetter(guiDataStore, resourceGetter), compositeConfig, editorFactory);
		for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				Group group = new Group(content, SWT.SHADOW_ETCHED_IN);
				group.setVisible(false);
				smallButtonIdToGroupMap.put(smallButtonDefn.id, group);
				group.setText(Strings.nullSafeToString(dataGetter.getDataFor(smallButtonDefn.titleId)));
				for (DisplayerDefn defn : smallButtonDefn.defns) {
					IDisplayer displayer = defn.createDisplayer(group, actionStore, actionContext);
					displayDefnToDisplayerMap.put(defn, displayer);
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
				ISmallDisplayer control = smallButtonIdToSmallDisplayerMap.get(smallButtonDefn.id);
				control.setValue(!visible);
			}
		});
		guiDataStore.addGuiDataListener(new IGuiDataListener() {
			@Override
			public void data(String entity, String url, Map<String, Object> context, Map<String, Object> data) {
				for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
					for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
						ISmallDisplayer smallDisplayer = smallButtonIdToSmallDisplayerMap.get(smallButtonDefn.id);
						smallDisplayer.data(dataGetter, entity, url, context, data);
						for (DisplayerDefn defn : smallButtonDefn.defns) {
							IDisplayer displayer = displayDefnToDisplayerMap.get(defn);
							defn.data(dataGetter, defn, displayer, entity, url, context, data);
						}
					}
				}
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
