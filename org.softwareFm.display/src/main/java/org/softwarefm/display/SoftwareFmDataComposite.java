package org.softwareFm.display;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataListener;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.selection.DisplaySelectionModel;
import org.softwareFm.display.selection.IDisplaySelectionListener;
import org.softwareFm.display.simpleButtons.SimpleButtonParent;
import org.softwareFm.display.smallButtons.ImageButtonConfig;
import org.softwareFm.display.smallButtons.SmallButtonDefn;
import org.softwareFm.softwareFmImages.Images;
import org.softwareFm.softwareFmImages.backdrop.BackdropAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class SoftwareFmDataComposite implements IHasComposite {

	private final Composite content;
	private final Composite topRow;
	private final DisplaySelectionModel displaySelectionModel;
	private final Map<String, ISmallDisplayer> smallButtonIdToSmallDisplayerMap = Maps.newMap(LinkedHashMap.class);
	private final Map<DisplayerDefn, IDisplayer> displayDefnToDisplayerMap = Maps.newMap(LinkedHashMap.class);
	// private final Map<String, List<IDisplayer>> smallButtonIdToDisplayerMap = Maps.newMap(LinkedHashMap.class);
	private final Map<String, Group> smallButtonIdToGroupMap = Maps.newMap(LinkedHashMap.class);
	private final Map<LargeButtonDefn, SimpleButtonParent> largeButtonidToButtonParent = Maps.newMap(LinkedHashMap.class);
	private final CompositeConfig compositeConfig;

	public SoftwareFmDataComposite(final Composite parent, final GuiDataStore guiDataStore, final CompositeConfig compositeConfig, final ActionStore actionStore, IEditorFactory editorFactory, IUpdateStore updateStore, ListEditorStore listEditorStore, ICallback<Throwable> exceptionHandler, final List<LargeButtonDefn> largeButtonDefns) {
		this.compositeConfig = compositeConfig;
		this.content = new Composite(parent, SWT.NULL);
		displaySelectionModel = new DisplaySelectionModel(exceptionHandler, largeButtonDefns);
		topRow = new Composite(content, SWT.NULL);
		topRow.setLayout(Swts.getHorizonalNoMarginRowLayout());
		ImageButtonConfig imageButtonConfig = compositeConfig.imageButtonConfig;
		SoftwareFmLayout layout = imageButtonConfig.layout;
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			SimpleButtonParent largeButton = new SimpleButtonParent(topRow, layout, SWT.BORDER);
			largeButtonidToButtonParent.put(largeButtonDefn, largeButton);
			largeButton.getButtonComposite().setLayoutData(new RowData(SWT.DEFAULT, layout.largeButtonHeight));
			for (final SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				ISmallDisplayer smallDisplayer = smallButtonDefn.smallButtonFactory.create( largeButton, smallButtonDefn, imageButtonConfig.withImage(smallButtonDefn.mainImageId));
				smallButtonIdToSmallDisplayerMap.put(smallButtonDefn.id, smallDisplayer);
				Control control = smallDisplayer.getControl();
				control.setLayoutData(new RowData(layout.smallButtonWidth, layout.smallButtonHeight));
				control.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						displaySelectionModel.select(smallButtonDefn.id);
						System.out.println("Selected: " + smallButtonDefn.id);
					}
				});
			}
		}
		final ActionContext actionContext = new ActionContext(guiDataStore, compositeConfig, editorFactory, updateStore, listEditorStore);
		for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				Group group = new Group(content, SWT.SHADOW_ETCHED_IN);
				group.setVisible(false);
				smallButtonIdToGroupMap.put(smallButtonDefn.id, group);
//				group.setText(Strings.nullSafeToString(guiDataStore.getDataFor(smallButtonDefn.titleId)));
				for (DisplayerDefn defn : smallButtonDefn.defns) {
					IDisplayer displayer = defn.createDisplayer(group, actionStore, actionContext);
					displayDefnToDisplayerMap.put(defn, displayer);
				}
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(group);
			}
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
		updateVisibleData(largeButtonDefns.get(0));
		displaySelectionModel.addDisplaySelectionListener(new IDisplaySelectionListener() {
			@Override
			public void smallButtonPressed(DisplaySelectionModel model, LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn) {
				updateVisibleData(largeButtonDefn);

				boolean visible = displaySelectionModel.getVisibleSmallButtonsId().contains(smallButtonDefn.id);
				ISmallDisplayer control = smallButtonIdToSmallDisplayerMap.get(smallButtonDefn.id);
				control.setValue(!visible);
			}
		});
		guiDataStore.addGuiDataListener(new IGuiDataListener() {
			@Override
			public void data(final String entity, final String url) {
				Swts.asyncExec(SoftwareFmDataComposite.this, new Runnable() {
					@Override
					public void run() {
						for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
							for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
								ISmallDisplayer smallDisplayer = smallButtonIdToSmallDisplayerMap.get(smallButtonDefn.id);
								smallDisplayer.data(guiDataStore, entity, url);
								for (DisplayerDefn defn : smallButtonDefn.defns) {
									IDisplayer displayer = displayDefnToDisplayerMap.get(defn);
									defn.data(guiDataStore, defn, displayer, entity, url);
								}
							}
						}
					}
				});
			}
		});
	}

	private void updateVisibleData(final LargeButtonDefn largeButtonDefn) {
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				for (Entry<LargeButtonDefn, SimpleButtonParent> entry : largeButtonidToButtonParent.entrySet()) {
					String imageName = BackdropAnchor.group(entry.getValue().size(), entry.getKey() != largeButtonDefn);
					entry.getValue().getButtonComposite().setBackgroundImage(Images.getImage(compositeConfig.imageRegistry, imageName));
				}
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
