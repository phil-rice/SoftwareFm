package org.softwareFm.display;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.browser.BrowserComposite;
import org.softwareFm.display.browser.IBrowser;
import org.softwareFm.display.browser.IBrowserConfigurator;
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
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.display.timeline.TimeLine;
import org.softwareFm.softwareFmImages.Images;
import org.softwareFm.softwareFmImages.backdrop.BackdropAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class SoftwareFmDataComposite implements IHasComposite {

	private final Map<LargeButtonDefn, SimpleButtonParent> largeButtonidToButtonParent = Maps.newMap(LinkedHashMap.class);
	private final Map<LargeButtonDefn, Composite> largeButtonDefnToComposite = Maps.newMap(LinkedHashMap.class);
	private final Map<SmallButtonDefn, ISmallDisplayer> smallButtonToSmallDisplayerMap = Maps.newMap(LinkedHashMap.class);
	private final Map<DisplayerDefn, IDisplayer> displayDefnToDisplayerMap = Maps.newMap(LinkedHashMap.class);
	private final Map<SmallButtonDefn, Composite> smallButtonDefnToCompositeMap = Maps.newMap(LinkedHashMap.class);

	private final Composite content;
	public BrowserComposite browserComposite;
	private StackLayout rightHandSideLayout;
	private Composite rightHandSide;
	private ExecutorService service;

	public SoftwareFmDataComposite(final Composite parent, ExecutorService service, final GuiDataStore guiDataStore, final CompositeConfig compositeConfig, final ActionStore actionStore, final IEditorFactory editorFactory, final IUpdateStore updateStore, final ListEditorStore listEditorStore, ICallback<Throwable> exceptionHandler, final List<LargeButtonDefn> largeButtonDefns, final List<IBrowserConfigurator> browserConfigurators, IPlayListGetter playListGetter) {
		this.service = service;
		content = Swts.newComposite(parent, SWT.NULL, "SoftwareFmDataComposite.content");
		content.setLayout(new FillLayout());
		ActionContext actionContext = new ActionContext(new IHasRightHandSide() {

			@Override
			public Control getControl() {
				return rightHandSide;
			}

			@Override
			public Composite getComposite() {
				return rightHandSide;
			}

			@Override
			public void makeVisible(Control control) {
				rightHandSideLayout.topControl = control;
				rightHandSide.layout();

			}

			@Override
			public Control getVisibleControl() {
				return rightHandSideLayout.topControl;
			}
		}, actionStore, guiDataStore, guiDataStore.getEntityToUrlGetter(), compositeConfig, editorFactory, updateStore, new IBrowser() {
			@Override
			public Future<String> processUrl(String feedType, String url) {
				return browserComposite.processUrl(feedType, url);
			}
		}, exceptionHandler);
		StackLayout stackLayout = new StackLayout();
		SashForm sash = new SashForm(content, SWT.HORIZONTAL);
		sash.setLayout(new FillLayout());

		Composite leftHandside = new Composite(sash, SWT.NULL);
		makeActionButtons(leftHandside, compositeConfig, largeButtonDefns).setLayoutData(Swts.makeGrabHorizonalAndFillGridData());
		makeDisplayers(leftHandside, largeButtonDefns, actionContext, stackLayout);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(leftHandside);
		
		makeRightHandSide(sash, actionContext, browserConfigurators);
		sash.setWeights(new int[] { 1, 2 });

		TimeLine timeLine = new TimeLine(browserComposite, playListGetter);
		setUpListeners(largeButtonDefns, actionContext, stackLayout, timeLine);
	}

	private Composite makeActionButtons(Composite parent, final CompositeConfig compositeConfig, final List<LargeButtonDefn> largeButtonDefns) {
		Composite topRow = Swts.newComposite(parent, SWT.NULL, "topRow");
		topRow.setLayout(Swts.getHorizonalNoMarginRowLayout());
		ImageButtonConfig imageButtonConfig = compositeConfig.imageButtonConfig;
		SoftwareFmLayout layout = imageButtonConfig.layout;
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			SimpleButtonParent largeButton = new SimpleButtonParent(topRow, layout, SWT.BORDER);
			largeButtonidToButtonParent.put(largeButtonDefn, largeButton);
			largeButton.getButtonComposite().setLayoutData(new RowData(SWT.DEFAULT, layout.largeButtonHeight));
			for (final SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				ISmallDisplayer smallDisplayer = smallButtonDefn.smallButtonFactory.create(largeButton, smallButtonDefn, imageButtonConfig.withImage(smallButtonDefn.mainImageId));
				smallButtonToSmallDisplayerMap.put(smallButtonDefn, smallDisplayer);
				Control control = smallDisplayer.getControl();
				control.setLayoutData(new RowData(layout.smallButtonWidth, layout.smallButtonHeight));
			}
		}
		return topRow;
	}

	private void makeRightHandSide(Composite parent, final ActionContext actionContext, List<IBrowserConfigurator> browserConfigurators) {
		rightHandSide = Swts.newComposite(parent, SWT.BORDER, "RightHandSide");
		rightHandSideLayout = new StackLayout();
		rightHandSide.setLayout(rightHandSideLayout);
		rightHandSide.setLayoutData(Swts.makeGrabHorizonalVerticalAndFillGridData());
		browserComposite = new BrowserComposite(rightHandSide, SWT.BORDER, service);
		rightHandSideLayout.topControl = browserComposite.getControl();
		for (IBrowserConfigurator configurator : browserConfigurators)
			configurator.configure(actionContext.compositeConfig, browserComposite);
		((Composite) browserComposite.getControl()).layout();
		browserComposite.getControl().setLayoutData(Swts.makeGrabHorizonalVerticalAndFillGridData());
	}

	private void makeDisplayers(Composite parent, final List<LargeButtonDefn> largeButtonDefns, final ActionContext actionContext, StackLayout stackLayout) {
		Composite displayerComposite = Swts.newComposite(parent, SWT.BORDER, "displayers");
		displayerComposite.setLayout(stackLayout);
		for (final LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			Composite largeButtonComposite = Swts.newComposite(displayerComposite, SWT.NULL, largeButtonDefn.id);
			largeButtonDefnToComposite.put(largeButtonDefn, largeButtonComposite);
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				Composite group = Swts.newComposite(largeButtonComposite, SWT.SHADOW_ETCHED_IN, smallButtonDefn.id);
				smallButtonDefnToCompositeMap.put(smallButtonDefn, group);
				// group.setText(Strings.nullSafeToString(guiDataStore.getDataFor(smallButtonDefn.titleId)));
				for (DisplayerDefn defn : smallButtonDefn.defns) {
					IDisplayer displayer = defn.createDisplayer(group, actionContext);
					displayDefnToDisplayerMap.put(defn, displayer);
				}
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(group);
			}
			Swts.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(largeButtonComposite, actionContext.compositeConfig.layout.dataMargin);

		}
		displayerComposite.setLayoutData(Swts.makeGrabHorizonalVerticalAndFillGridData());
	}

	private void setUpListeners(final List<LargeButtonDefn> largeButtonDefns, final ActionContext actionContext, final StackLayout stackLayout, final TimeLine timeLine) {
		final GuiDataStore guiDataStore = (GuiDataStore) actionContext.dataGetter;
		ICallback<Throwable> exceptionHandler = actionContext.exceptionHandler;
		final DisplaySelectionModel displaySelectionModel = new DisplaySelectionModel(exceptionHandler, largeButtonDefns);
		displaySelectionModel.addDisplaySelectionListener(new IDisplaySelectionListener() {
			@Override
			public void smallButtonPressed(DisplaySelectionModel model, LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn) {
				ImageRegistry imageRegistry = actionContext.compositeConfig.imageRegistry;
				updateVisibleData(imageRegistry, largeButtonDefn, displaySelectionModel.getVisibleSmallButtonsId(), stackLayout);
				boolean visible = displaySelectionModel.getVisibleSmallButtonsId().contains(smallButtonDefn.id);
				ISmallDisplayer control = smallButtonToSmallDisplayerMap.get(smallButtonDefn);
				control.setValue(!visible);
				actionContext.editorFactory.cancel();
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
								ISmallDisplayer smallDisplayer = smallButtonToSmallDisplayerMap.get(smallButtonDefn);
								smallDisplayer.data(guiDataStore, entity, url);
								for (DisplayerDefn defn : smallButtonDefn.defns) {
									IDisplayer displayer = displayDefnToDisplayerMap.get(defn);
									defn.data(actionContext, defn, displayer, entity, url);
								}
							}
						}
					}
				});
				if (entity.equals("artifact")) {
					try {
						timeLine.forgetPlayList(url);
						timeLine.selectAndNext(url);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns) {
			for (final SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
				ISmallDisplayer smallDisplayer = smallButtonToSmallDisplayerMap.get(smallButtonDefn);
				Control control = smallDisplayer.getControl();
				control.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						displaySelectionModel.select(smallButtonDefn.id);
					}
				});
			}
		}
		LargeButtonDefn firstButton = largeButtonDefns.get(0);
		updateVisibleData(actionContext.compositeConfig.imageRegistry, firstButton, displaySelectionModel.getVisibleSmallButtonsId(), stackLayout);
	}

	private void updateVisibleData(final ImageRegistry imageRegistry, final LargeButtonDefn largeButtonDefn, final List<String> visibleIds, final StackLayout stackLayout) {
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				Composite composite = largeButtonDefnToComposite.get(largeButtonDefn);
				stackLayout.topControl = composite;
				for (Entry<LargeButtonDefn, SimpleButtonParent> entry : largeButtonidToButtonParent.entrySet()) {
					String imageName = BackdropAnchor.group(entry.getValue().size(), entry.getKey() != largeButtonDefn);
					entry.getValue().getButtonComposite().setBackgroundImage(Images.getImage(imageRegistry, imageName));
				}

				List<Control> visible = Lists.newList();
				List<Control> inVisible = Lists.newList();
				for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns) {
					Composite group = smallButtonDefnToCompositeMap.get(smallButtonDefn);
					boolean b = visibleIds.contains(smallButtonDefn.id);
					group.setVisible(b);
					if (b)
						visible.add(group);
					else
						inVisible.add(group);
				}

				Control lastVisible = Swts.setAfter(visible, null);
				Swts.setAfter(inVisible, lastVisible);
				composite.getParent().layout();
				composite.getParent().redraw();
				composite.layout();
				composite.redraw();
				// Swts.asyncExec(SoftwareFmDataComposite.this, new Runnable() {
				// @Override
				// public void run() {
				// Swts.layoutDump(content);
				// }
				// });
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
