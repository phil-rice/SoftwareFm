/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.core.swt;

import java.io.File;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;
import net.miginfocom.swt.MigLayout;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.softwarefm.core.constants.SwtConstants;
import org.softwarefm.core.labelAndText.IButtonConfig;
import org.softwarefm.utilities.arrays.ArrayHelper;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.constants.UtilityConstants;
import org.softwarefm.utilities.constants.UtilityMessages;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.future.GatedMockFuture;
import org.softwarefm.utilities.indent.Indent;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class Swts {

	public static class Dispatch {

		public static void dispatchUntilTimeoutOrLatch(Display display, final CountDownLatch latch, long delay) {
			try {
				long start = System.currentTimeMillis();
				dispatchUntilQueueEmpty(display);
				while (!latch.await(10, TimeUnit.MILLISECONDS)) {
					if (System.currentTimeMillis() > delay + start)
						Assert.fail();
					dispatchUntilQueueEmpty(display);
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				throw WrappedException.wrap(e);
			}
		}

		public static void kickAndDispatch(Display display, Future<?> future) {
			GatedMockFuture<?, ?> gatedMockFuture = (GatedMockFuture<?, ?>) future;
			gatedMockFuture.kick();
			dispatchUntilQueueEmpty(display);
		}

		public static void dispatchUntilQueueEmpty(Display display) {
			Swts.dispatchUntilQueueEmpty(display);
		}

		public static <T> T callInDispatch(Display display, final Callable<T> callable) {
			final AtomicReference<T> result = new AtomicReference<T>();
			display.syncExec(new Runnable() {

				public void run() {
					try {
						result.set(callable.call());
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}
			});

			return result.get();
		}

		public static void dispatchUntil(Display display, long delay, Callable<Boolean> callable) {
			long startTime = System.currentTimeMillis();
			try {
				dispatchUntilQueueEmpty(display);
				while (!callable.call() && System.currentTimeMillis() < startTime + delay) {
					dispatchUntilQueueEmpty(display);
					Thread.sleep(200);
				}
				dispatchUntilQueueEmpty(display);
				checkAtEnd(display, callable);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		private static void checkAtEnd(Display display, Callable<Boolean> callable) {
			if (!callInDispatch(display, callable))
				Assert.fail();
		}

	}

	public static class Size {

		public static Listener resizeMeToParentsSize(final Control control) {
			Size.setSizeFromClientArea(control);

			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					Size.setSizeFromClientArea(control);
				}
			};
			control.getParent().addListener(SWT.Resize, listener);
			Swts.redrawAllChildren(control);
			return listener;
		}

		static int globalId = 0;

		public static Listener resizeMeToParentsSizeWithLayout(final IHasComposite hasComposite) {
			final Composite composite = hasComposite.getComposite();
			Size.setSizeFromClientArea(composite);

			Listener listener = new Listener() {
				// int id = globalId++;

				public void handleEvent(Event event) {
					// System.out.println("SWT/resizeMeToParentsSizeWithLayout " + id + boundsUpToShell(composite));
					Size.setSizeFromClientArea(composite);
					composite.layout();
					Swts.redrawAllChildren(composite);
					// System.out.println("   end SWT/resizeMeToParentsSizeWithLayout " + id + boundsUpToShell(composite));
				}
			};
			composite.getParent().addListener(SWT.Resize, listener);
			return listener;
		}

		public static Listener resizeMeToParentsSizeWithTopMargin(final Control control, final int topMargin) {
			Size.setSizeAndLocationFromParentsSizeWithTopMargin(control, topMargin);

			Listener listener = new Listener() {

				public void handleEvent(Event event) {
					Size.setSizeAndLocationFromParentsSizeWithTopMargin(control, topMargin);
				}

			};
			control.getParent().addListener(SWT.Resize, listener);
			return listener;
		}

		private static void setSizeAndLocationFromParentsSizeWithTopMargin(final Control control, final int topMargin) {
			Rectangle clientArea = control.getParent().getClientArea();
			control.setLocation(clientArea.x, clientArea.y + topMargin);
			control.setSize(clientArea.width, clientArea.height - topMargin);
		}

		public static void removeOldResizeListener(final Control control, Listener listener) {
			if (listener != null)
				control.removeListener(SWT.Resize, listener);
		}

		public static void setSizeToComputedAndLayout(IHasControl left, int wHint, int hHint) {
			Control control = left.getControl();
			Size.setSizeToComputedSize(control, wHint, hHint);
			if (control instanceof Composite)
				((Composite) control).layout();
		}

		public static void setSizeToComputedAndLayout(IHasControl left, Rectangle rectangle) {
			Control control = left.getControl();
			Point computedSize = control.computeSize(rectangle.width, rectangle.height);
			control.setSize(computedSize);
			if (control instanceof Composite)
				((Composite) control).layout();
		}

		public static void setSizeFromClientArea(IHasComposite parent, IHasControl child) {
			Rectangle clientArea = parent.getComposite().getClientArea();
			child.getControl().setSize(clientArea.width, clientArea.height);
		}

		public static Rectangle setSizeFromClientArea(Control child) {
			Rectangle clientArea = child.getParent().getClientArea();
			child.setSize(clientArea.width, clientArea.height);
			if (child instanceof Composite)
				((Composite) child).layout();
			return clientArea;
			// child.redraw();

		}

		public static void setSizeToComputedSize(Control c, int wHint, int hHint) {
			Point size = c.computeSize(wHint, hHint);
			c.setSize(size);
		}

	}

	public static class Show {

		public static void display(String title, IFunction1<Composite, Composite> builder) {
			try {
				Display display = new Display();
				Shell shell = new Shell(display);
				shell.setSize(600, 400);
				shell.setText(title);
				shell.setLayout(new FillLayout());
				builder.apply(shell);
				shell.open();
				Swts.layoutDump(shell);
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				display.dispose();
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static void displayFormLayout(String title, IFunction1<Composite, Composite> builder) {
			try {
				Display display = new Display();
				Shell shell = new Shell(display);
				shell.setSize(1300, 500);
				shell.setText(title);
				shell.setLayout(new FormLayout());
				Composite composite = builder.apply(shell);
				FormData fd = new FormData();
				fd.bottom = new FormAttachment(100, 0);
				fd.right = new FormAttachment(100, 0);
				fd.top = new FormAttachment(0, 0);
				fd.left = new FormAttachment(0, 0);
				composite.setLayoutData(fd);
				shell.open();
				Swts.layoutDump(shell);
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				display.dispose();
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static <T extends IHasControl> void xUnit(String title, final File root, final String extension, final ISituationListAndBuilder<T, String> builder) {
			Swts.Show.displayFormLayout(title, new IFunction1<Composite, Composite>() {
				public Composite apply(Composite from) throws Exception {
					Callable<? extends Iterable<String>> situations = new Callable<Iterable<String>>() {
						public Iterable<String> call() throws Exception {
							return Iterables.map(Files.walkChildrenOf(root, Files.extensionFilter(extension)), Files.toFileName());
						}
					};
					final SituationListAnd<T, String> result = new SituationListAnd<T, String>(from, situations, builder);
					result.addListener(new ISituationListListener<T>() {
						public void selected(T hasControl, String selectedItem) throws Exception {
							File file = new File(root, selectedItem);
							String value = Files.getText(file);
							builder.selected(hasControl, selectedItem, value);
							result.setText(value);
						}
					});
					result.selectFirst();
					return result.getComposite();
				}
			});
		}

		public static <T extends IHasControl, V> void xUnit(String title, final ISituationListAndBuilder<T, V> builder, final Map<String, V> situationMap) {
			Swts.Show.displayFormLayout(title, new IFunction1<Composite, Composite>() {
				public Composite apply(Composite from) throws Exception {
					final Callable<? extends Iterable<String>> situationsCallable = new Callable<Iterable<String>>() {
						public Iterable<String> call() throws Exception {
							return situationMap.keySet();
						}
					};
					final SituationListAnd<T, V> result = new SituationListAnd<T, V>(from, situationsCallable, builder);
					result.addListener(new ISituationListListener<T>() {
						public void selected(T hasControl, String selectedItem) throws Exception {
							V value = situationMap.get(selectedItem);
							builder.selected(hasControl, selectedItem, value);
							result.setText(Strings.nullSafeToString(value));
						}
					});

					result.selectFirst();
					return result.getComposite();
				}
			});
		}

	}

	public static class Buttons {

		public static void makeButtonFromMainMethod(Composite composite, final Class<?> classWithMain) {
			org.eclipse.swt.widgets.Button button = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH);
			button.setText(classWithMain.getSimpleName());
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					new Thread() {

						@Override
						public void run() {
							try {
								Method method = classWithMain.getMethod("main", String[].class);
								method.invoke(null, new Object[] { new String[0] });
							} catch (Exception e1) {
								throw WrappedException.wrap(e1);
							}
						}
					}.start();
				}
			});
		}

		public static org.eclipse.swt.widgets.Button makePushButtonAtStart(Composite parent, IResourceGetter resourceGetter, String titleKey, IButtonConfig buttonConfig) {
			Button button = makePushButton(parent, resourceGetter, titleKey, buttonConfig);
			Control[] children = parent.getChildren();
			if (children.length > 0)
				button.moveAbove(children[0]);
			return button;

		}

		public static org.eclipse.swt.widgets.Button makePushButton(Composite parent, IResourceGetter resourceGetter, String titleKey, final IButtonConfig buttonConfig) {
			return Buttons.makePushButton(parent, resourceGetter, titleKey, true, buttonConfig);
		}

		public static org.eclipse.swt.widgets.Button makePushButton(Composite parent, String text, final IButtonConfig buttonConfig) {
			return Buttons.makePushButton(parent, null, text, false, buttonConfig);
		}

		public static org.eclipse.swt.widgets.Button makePushButton(Composite parent, IResourceGetter resourceGetter, String titleOrKey, boolean titleIsKey, final IButtonConfig buttonConfig) {
			return Buttons.makePushButton(parent, SWT.PUSH, resourceGetter, titleOrKey, titleIsKey, buttonConfig);
		}

		public static void press(Control control) {
			if (control instanceof Label)
				control.notifyListeners(SWT.MouseUp, new Event());
			else if (control instanceof Button)
				control.notifyListeners(SWT.Selection, new Event());
			else
				throw new IllegalArgumentException(control.getClass().getSimpleName());
		}

		public static org.eclipse.swt.widgets.Button makePushButton(Composite parent, int style, IResourceGetter resourceGetter, String titleOrKey, boolean titleIsKey, final IButtonConfig buttonConfig) {
			org.eclipse.swt.widgets.Button button = new org.eclipse.swt.widgets.Button(parent, style);
			String title = titleIsKey ? IResourceGetter.Utils.getOrException(resourceGetter, titleOrKey) : titleOrKey;
			button.setText(title);
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						buttonConfig.execute();
					} catch (Exception e1) {
						throw WrappedException.wrap(e1);
					}
				}
			});
			return button;
		}

		public static Label makeImageButton(Composite parent, Image image, final Runnable runnable) {
			Label label = new Label(parent, SWT.NULL);
			label.setImage(image);
			label.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseUp(MouseEvent e) {
					runnable.run();
				}
			});
			return label;
		}

		public static Label makeImageButtonAtStart(Composite parent, IFunction1<String, Image> imageFn, String name, final Runnable runnable) {
			Label label = new Label(parent, SWT.NULL);
			Image image = Functions.call(imageFn, name);
			if (image == null)
				throw new IllegalArgumentException(name);
			label.setImage(image);
			label.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseUp(MouseEvent e) {
					runnable.run();
				}
			});
			Control[] children = parent.getChildren();
			if (children.length > 0)
				label.moveAbove(children[0]);
			return label;
		}

		public static Button makeMigCheckboxButton(Composite parent, String text, String constraint) {
			Button button = new Button(parent, SWT.CHECK);
			button.setText(text);
			button.setLayoutData(constraint);
			return button;
		}

		public static Button makeMigPushButton(Composite parent, String text, String constraint) {
			Button button = new Button(parent, SWT.PUSH);
			button.setText(text);
			button.setLayoutData(constraint);
			return button;
		}

		public static Button makeRadioButton(Composite parent, IResourceGetter resourceGetter, String key, final Runnable runnable) {
			String title = IResourceGetter.Utils.getOrException(resourceGetter, key);
			return makeRadioButton(parent, title, runnable);
		}

		public static Button makeRadioButton(Composite parent, String text, final Runnable runnable) {
			Button button = new Button(parent, SWT.RADIO);
			button.setText(text);
			button.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event event) {
					runnable.run();
				}
			});
			return button;
		}

		public static void selectRadioButton(Composite composite, int index) {
			// ok this sucks, see http://stackoverflow.com/questions/5835618/swt-set-radio-buttons-programmatically
			Control[] children = composite.getChildren();
			Button button = (Button) children[index];
			for (Control child : children) {
				if (child instanceof Button)
					((Button) child).setSelection(child == button);
			}
		}
	}

	public static class Grid {

		public static void addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(Composite composite, int margin) {
			GridLayout layout = new GridLayout();
			layout.marginWidth = margin;
			layout.marginHeight = margin;
			layout.verticalSpacing = 5;
			composite.setLayout(layout);
			for (Control control : composite.getChildren()) {
				GridData data = Grid.makeGrabHorizonalAndFillGridData();
				control.setLayoutData(data);
			}
		}

		public static void addGrabHorizontalAndFillGridDataToAllChildren(Composite composite) {
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(composite, 0);
		}

		public static void addGrabHorizontalAndFillGridDataToAllChildrenWithHeightHint(Composite composite, int heightHint) {
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
			layout.marginHeight = 0;
			composite.setLayout(layout);
			for (Control control : composite.getChildren()) {
				GridData data = Grid.makeGrabHorizonalAndFillGridDataWithHeight(heightHint);
				control.setLayoutData(data);
			}
		}

		public static void addGrabHorizontalAndFillGridDataToAllChildrenWithHeightWidthHint(Composite composite, int heightHint, int widthHint) {
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
			layout.marginHeight = 0;
			composite.setLayout(layout);
			for (Control control : composite.getChildren()) {
				GridData data = Grid.makeGrabHorizonalAndFillGridDataWithHeightWidth(heightHint, widthHint);
				control.setLayoutData(data);
			}
		}

		private static GridData makeGrabHorizonalAndFillGridDataWithHeightWidth(int heightHint, int widthHint) {
			GridData result = Grid.makeGrabHorizonalAndFillGridData();
			result.heightHint = heightHint;
			result.widthHint = widthHint;
			return result;

		}

		public static GridData makeGrabHorizonalVerticalAndFillGridData() {
			GridData data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			return data;
		}

		public static GridData makeGrabHorizonalAndFillGridData() {
			GridData data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			data.verticalAlignment = SWT.TOP;

			return data;
		}

		public static Layout getGridLayoutWithoutMargins(int columns) {
			GridLayout gridLayout = new GridLayout(columns, false);
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 0;
			gridLayout.verticalSpacing = 0;
			gridLayout.horizontalSpacing = 0;
			return gridLayout;
		}

		public static GridData makeGrabHorizonalAndFillGridDataWithHeight(int heightHint) {
			GridData result = Swts.Grid.makeGrabHorizonalAndFillGridData();
			result.heightHint = heightHint;
			return result;

		}

		public static void addGrabVerticalToGridData(Control control, boolean fill) {
			GridData data = (GridData) control.getLayoutData();
			data.grabExcessVerticalSpace = true;
			if (fill)
				data.verticalAlignment = SWT.FILL;
		}

		public static void addGrabHorizontalAndFillGridDataToAllChildrenWithLastGrabingVertical(Composite composite) {
			addGrabHorizontalAndFillGridDataToAllChildren(composite);
			Control[] children = composite.getChildren();
			if (children.length > 0) {
				Control lastChild = children[children.length - 1];
				addGrabVerticalToGridData(lastChild, true);
			}

		}

		public static void oneRowWithControlGrabbingWidth(Composite composite, Control control) {
			Control[] children = composite.getChildren();
			composite.setLayout(new GridLayout(children.length, false));
			control.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		}

	}

	public static class Row {

		public static RowLayout getHorizonalMarginRowLayout(int margin) {
			RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
			rowLayout.marginWidth = 0;
			rowLayout.marginHeight = 0;
			rowLayout.marginTop = margin;
			rowLayout.marginBottom = margin;
			rowLayout.marginLeft = margin;
			rowLayout.marginRight = margin;
			rowLayout.fill = true;
			rowLayout.justify = false;
			rowLayout.spacing = 0;
			return rowLayout;
		}

		public static RowLayout getHorizonalNoMarginRowLayout() {
			return Swts.Row.getHorizonalMarginRowLayout(0);
		}

		public static <C extends Control> void setRowDataFor(int width, int height, C... controls) {
			for (Control control : controls)
				control.setLayoutData(new RowData(width, height));

		}

		public static RowData rowDataWithWidth(int width) {
			RowData rowData = new RowData(width, SWT.DEFAULT);
			return rowData;
		}

	}

	public static ScrolledComposite newScrolledComposite(Composite parent, int style, final String description) {
		return new ScrolledComposite(parent, style) {

			@Override
			public String toString() {
				return description + "." + super.toString();
			}
		};
	}

	public static Composite newComposite(Composite parent, int style, final String description) {
		return new Composite(parent, style) {

			@Override
			public String toString() {
				return description + "." + super.toString();
			}
		};
	}

	public static Composite newCompositeWithDispose(Composite parent, int style, final String description, final IDispose dispose) {
		return new Composite(parent, style) {

			@Override
			public void dispose() {
				System.out.println("Disposing of " + description);
				dispose.dispose();
				super.dispose();
			}

			@Override
			public String toString() {
				return description + "." + super.toString();
			}
		};
	}

	public static SashForm newSashForm(Composite parent, int style, final String description) {
		return new SashForm(parent, style) {

			@Override
			public String toString() {
				return description + "." + super.toString();
			}
		};
	}

	public static void redrawAllChildren(Control control) {
		walkChildren(control, new ICallback<Control>() {

			public void process(Control t) throws Exception {
				t.redraw();
			}
		});
	}

	public static void walkChildren(Control control, ICallback<Control> callback) {
		if (control instanceof Composite)
			for (Control child : ((Composite) control).getChildren()) {
				ICallback.Utils.call(callback, child);
				walkChildren(child, callback);
			}
	}

	public static <T extends Control> List<T> findChildrenWithClass(Control control, final Class<T> clazz) {
		final List<T> result = Lists.newList();
		walkChildren(control, new ICallback<Control>() {
			@SuppressWarnings("unchecked")
			public void process(Control t) throws Exception {
				if (clazz.isAssignableFrom(t.getClass()))
					result.add((T) t);
			}
		});
		return result;
	}

	public static Group newGroup(Composite parent, int style, final String description) {
		return new Group(parent, style) {

			@Override
			public String toString() {
				return description + "." + super.toString();
			}

			@Override
			protected void checkSubclass() {
			}
		};
	}

	public static StyledText makeHelpDisplayer(Composite parent) {
		StyledText text = new StyledText(parent, SWT.WRAP | SWT.BORDER);
		text.setEditable(false);
		text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		return text;

	}

	public static void setHelpText(StyledText help, IResourceGetter resourceGetter, String helpKey) {
		help.setText(helpKey == null ? "" : Strings.nullSafeToString(resourceGetter.getStringOrNull(helpKey)));
		help.getParent().layout();
	}

	public static void setFontStyle(Label label, int fontStyle) {
		Font font = label.getFont();
		FontData fontData = font.getFontData()[0];
		Font newFont = new Font(label.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), fontStyle));
		label.setFont(newFont);
	}

	public static void setFontStyle(StyledText text, int fontStyle) {
		Font font = text.getFont();
		FontData fontData = font.getFontData()[0];
		Font newFont = new Font(text.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), fontStyle));
		text.setFont(newFont);
	}

	public static Control setAfter(List<Control> controls, Control firstControl) {
		for (Control control : controls) {
			control.moveBelow(firstControl);
			firstControl = control;
		}
		return firstControl;
	}

	public static <K, V extends Composite> void sortVisibilityForComposites(Map<K, V> map, IFunction1<K, Boolean> acceptor) {
		sortVisibility(map, Functions.<V, Control> toSingletonList(), acceptor);
	}

	public static <K> void sortVisibilityForHasControlList(Map<K, List<IHasControl>> map, IFunction1<K, Boolean> acceptor) {
		sortVisibility(map, IHasControl.Utils.toListOfControls(), acceptor);
	}

	public static <K, V> void sortVisibility(Map<K, V> map, IFunction1<V, List<Control>> convertor, IFunction1<K, Boolean> acceptor) {
		try {
			final List<Control> visibleControls = Lists.newList();
			final List<Control> invisibleControls = Lists.newList();
			for (Entry<K, V> entry : map.entrySet()) {
				V value = entry.getValue();
				for (Control control : convertor.apply(value)) {
					boolean isVisible = acceptor.apply(entry.getKey());
					control.setVisible(isVisible);
					if (isVisible)
						visibleControls.add(control);
					else
						invisibleControls.add(control);
				}
			}
			Control lastVisible = Swts.setAfter(visibleControls, null);
			for (Control control : invisibleControls)
				control.moveBelow(lastVisible);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void makeOnlyOneVisible(Composite content, Control control) {
		final List<Control> visibleControls = Lists.newList();
		final List<Control> invisibleControls = Lists.newList();
		for (Control child : content.getChildren()) {
			boolean visible = child == control;
			child.setVisible(visible);
			if (visible)
				visibleControls.add(child);
			else
				invisibleControls.add(child);
		}
		Control lastVisible = Swts.setAfter(visibleControls, null);
		for (Control invisibleControl : invisibleControls)
			invisibleControl.moveBelow(lastVisible);
	}

	public static TableEditor addEditor(Table table, int row, int col, Control control) {
		TableItem[] items = table.getItems();
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.setEditor(control, items[row], col);
		return editor;
	}

	public static String layoutAsString(Control control) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(control);
		Point location = control.getLocation();
		Point size = control.getSize();
		buffer.append(" Loc: " + location.x + ", " + location.y);
		buffer.append(" Size: " + size.x + ", " + size.y);
		if (control instanceof Composite)
			buffer.append(" Layout: " + ((Composite) control).getLayout());
		buffer.append(" LayoutData: " + control.getLayoutData());
		return buffer.toString();
	}

	public static void layoutDump(Control control) {
		layoutDump(control, new Indent());
	}

	public static void layoutDump(Control control, Indent indent) {
		System.out.println(indent + control.getClass().getSimpleName() + "(Visible: " + control.isVisible() + ": " + layoutAsString(control) + getStackLayoutString(control));
		if (control.getLayoutData() instanceof GridData)
			if (!(control.getParent().getLayout() instanceof GridLayout))
				System.err.println("Layout issues: child has GridData parent not GridLayout");
		if (control.getLayoutData() instanceof RowData)
			if (!(control.getParent().getLayout() instanceof RowLayout))
				System.err.println("Layout issues: child has RowData parent not RowLayout");
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			for (Control nested : composite.getChildren()) {
				Indent indented = indent.indent();
				layoutDump(nested, indented);
			}
		}
	}

	private static String getStackLayoutString(Control control) {
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			if (composite.getLayout() instanceof StackLayout) {
				StackLayout stackLayout = (StackLayout) composite.getLayout();
				Control topControl = stackLayout.topControl;
				Control[] children = composite.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (control == children[i])
						return "StackLayout[" + i + "]";
				}
				return "StackLayout[" + topControl + "]";
			}
		}
		return "";
	}

	public static void removeAllChildren(Composite composite) {
		if (!composite.isDisposed())
			for (Control control : composite.getChildren())
				control.dispose();

	}

	public static List<Control> children(Composite composite) {
		return Arrays.asList(composite.getChildren());
	}

	public static List<Control> allChildren(Composite composite) {
		List<Control> result = Lists.newList();
		for (Control c : composite.getChildren()) {
			result.add(c);
			if (c instanceof Composite)
				result.addAll(allChildren((Composite) c));
		}
		return result;
	}

	public static void assertHasChildrenInOrder(Composite composite, Control... controls) {
		List<Control> children = children(composite);
		int lastIndex = -1;
		for (Control c : controls) {
			int index = children.indexOf(c);
			if (index == -1)
				Assert.fail("Could not find " + c + " in " + children);
			if (index < lastIndex)
				Assert.fail("Child " + index + "/" + c + " is in wrong order\nExpected " + controls + "\naActual: " + children);

		}
	}

	public static IFunction1<String, Image> imageFn(final ImageRegistry imageRegistry) {
		return new IFunction1<String, Image>() {

			public Image apply(String from) throws Exception {
				return imageRegistry.get(from);
			}
		};
	}

	public static void asyncExec(IHasControl hasControl, final Runnable runnable) {
		final Control control = hasControl.getControl();
		if (!control.isDisposed())
			control.getDisplay().asyncExec(new Runnable() {

				public void run() {
					if (!control.isDisposed())
						runnable.run();
				}
			});
	}

	public static void asyncExec(final Control control, final Runnable runnable) {
		if (!control.isDisposed())
			control.getDisplay().asyncExec(new Runnable() {

				public void run() {
					if (!control.isDisposed())
						runnable.run();
				}
			});

	}

	public static void syncExec(IHasControl hasControl, final Runnable runnable) {
		final Control control = hasControl.getControl();
		if (!control.isDisposed())
			control.getDisplay().syncExec(new Runnable() {

				public void run() {
					if (!control.isDisposed())
						runnable.run();
				}
			});
	}

	public static void syncExec(final Composite control, final Runnable runnable) {
		if (!control.isDisposed())
			control.getDisplay().syncExec(new Runnable() {

				public void run() {
					if (!control.isDisposed())
						runnable.run();
				}
			});
	}

	public static void dispatchUntilQueueEmpty(Display display) {
		while (display.readAndDispatch())
			doNothing();
	}

	private static void doNothing() {
	}

	public static void removeChildrenAfter(Composite composite, Control control) {
		Control[] children = composite.getChildren();
		int index = ArrayHelper.findIndexOf(children, control);
		if (index != -1)
			for (int i = index + 1; i < children.length; i++)
				children[i].dispose();
	}

	public static Color makeColor(Device device, IResourceGetter resourceGetter, String colorKey) {
		String raw = IResourceGetter.Utils.getOrException(resourceGetter, colorKey);
		List<String> strings = Strings.splitIgnoreBlanks(raw, ",");
		if (strings.size() != 3)
			throw new IllegalStateException(MessageFormat.format(SwtConstants.illegalColorString, colorKey, raw));
		int r = Integer.parseInt(strings.get(0));
		int g = Integer.parseInt(strings.get(1));
		int b = Integer.parseInt(strings.get(2));
		return new Color(device, r, g, b);
	}

	public static IFunction1<Composite, IHasControl> labelFn(final String text) {
		return new IFunction1<Composite, IHasControl>() {

			public IHasControl apply(Composite from) throws Exception {
				Label label = new Label(from, SWT.NULL);
				label.setText(text);
				return IHasControl.Utils.toHasControl(label);
			}
		};
	}

	public static IFunction1<Composite, IHasControl> styledTextFn(final String text, final int style) {
		return new IFunction1<Composite, IHasControl>() {

			public IHasControl apply(Composite from) throws Exception {
				StyledText styledText = new StyledText(from, style);
				styledText.setText(text);
				return IHasControl.Utils.toHasControl(styledText);
			}
		};
	}

	public static void layoutIfComposite(Control control) {
		if (control instanceof Composite)
			((Composite) control).layout();

	}

	public static String boundsUpToShell(Control control) {
		StringBuffer buffer = new StringBuffer();
		boundsUpToShell(buffer, control);
		return buffer.toString();
	}

	private static void boundsUpToShell(StringBuffer buffer, Control control) {
		if (buffer.length() > 0)
			buffer.append(" / ");
		buffer.append(control.getClass().getSimpleName() + ": " + control.getBounds());
		if (control.getParent() == null)
			return;
		else {
			Composite parent = control.getParent();
			boundsUpToShell(buffer, parent);
		}

	}

	public static String clientAreasUpToShell(Control control) {
		StringBuffer buffer = new StringBuffer();
		clientAreasUpToShell(buffer, control);
		return buffer.toString();
	}

	private static void clientAreasUpToShell(StringBuffer buffer, Control control) {
		if (buffer.length() > 0)
			buffer.append(" / ");
		String value = control instanceof Composite ? ((Composite) control).getClientArea().toString() : "na";
		buffer.append(control.getClass().getSimpleName() + ": " + value);
		if (control.getParent() == null)
			return;
		else {
			Composite parent = control.getParent();
			clientAreasUpToShell(buffer, parent);
		}
	}

	public static Button findButtonWithText(List<Button> buttons, String string) {
		for (Button button : buttons)
			if (string.equals(button.getText()))
				return button;
		throw new IllegalArgumentException(MessageFormat.format(UtilityConstants.cannotFindButtonWithText, string, buttons));
	}

	public static Layout titleAndContentLayout(final int titleHeight) {
		return new Layout() {

			@Override
			protected void layout(Composite composite, boolean flushCache) {
				Control[] children = composite.getChildren();
				assert children.length == 2 : Arrays.asList(children);
				Control title = children[0];
				Control content = children[1];
				Rectangle ca = composite.getClientArea();
				title.setBounds(ca.x, ca.y, ca.width, titleHeight);
				content.setBounds(ca.x, ca.y + titleHeight, ca.width, ca.height - titleHeight);
			}

			@Override
			protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
				Control[] children = composite.getChildren();
				assert children.length == 2;
				Control content = children[1];
				Point size = content.computeSize(wHint, hHint);
				return new Point(size.x, size.y + titleHeight);
			}
		};

	}

	public static Layout contentAndButtonBarLayout() {
		return new Layout() {

			@Override
			protected void layout(Composite composite, boolean flushCache) {
				Control[] children = composite.getChildren();
				assert children.length == 2 : Arrays.asList(children);
				Control content = children[0];
				Control buttons = children[1];
				Point buttonsSize = buttons.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				Rectangle ca = composite.getClientArea();
				content.setBounds(ca.x, ca.y, ca.width, ca.height - buttonsSize.y);
				buttons.setBounds(ca.x, ca.y + ca.height - buttonsSize.y, ca.width, buttonsSize.y);
			}

			@Override
			protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
				Control[] children = composite.getChildren();
				assert children.length == 2;
				Control content = children[0];
				Control buttons = children[1];
				Point contentSize = content.computeSize(wHint, hHint);
				Point buttonsSize = buttons.computeSize(wHint, hHint);
				return new Point(Math.max(contentSize.x, buttonsSize.x), contentSize.y + buttonsSize.y);
			}
		};

	}

	public static Layout titleAndRhsLayout(final int rightMargin) {
		return new Layout() {

			@Override
			protected void layout(Composite composite, boolean flushCache) {
				Control[] children = composite.getChildren();
				assert children.length == 2 : Arrays.asList(children);
				Control lhs = children[0];
				Control rhs = children[1];
				int rhsWidth = children[1].computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
				Rectangle ca = composite.getClientArea();
				lhs.setBounds(ca.x, ca.y, ca.width - rhsWidth - rightMargin, ca.height);
				rhs.setBounds(ca.x + ca.width - rhsWidth - rightMargin, ca.y, rhsWidth, ca.height);
			}

			@Override
			protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
				Control[] children = composite.getChildren();
				assert children.length == 2;
				Control lhs = children[0];
				Control rhs = children[1];
				Point lhSize = lhs.computeSize(wHint, hHint);
				Point rhSize = rhs.computeSize(wHint, hHint);
				return new Point(lhSize.x + rhSize.x + rightMargin, Math.max(lhSize.y, rhSize.y));
			}
		};
	}

	public static void addMenuTo(IHasControl hasControl, IFunction1<Control, Menu> builder) {
		Control control = hasControl.getControl();
		Menu menu = Functions.call(builder, control);
		control.setMenu(menu);
	}

	public static void addMenu(Menu menu, IResourceGetter resourceGetter, String key, final Runnable runnable) {
		String text = IResourceGetter.Utils.getOrException(resourceGetter, key);
		MenuItem item = new MenuItem(menu, SWT.NULL);
		item.setText(text);
		item.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				runnable.run();
			}
		});
	}

	public static Point computeSizeForVerticallyStackedControlWithIndent(int wHint, int hHint, Control... controls) {
		Point raw = Swts.computeSizeForVerticallyStackedControl(wHint, hHint, controls);
		return new Point(raw.x + 2 * SwtConstants.xIndent, raw.y + (controls.length + 1) * SwtConstants.yIndent);
	}

	public static Point computeSizeForHorizontallyStackedCompositesWithIndent(int wHint, int hHint, Control... controls) {
		Point raw = Swts.computeSizeForHorizontallyStackedComposites(wHint, hHint, controls);
		return new Point(raw.x + (controls.length + 1) * SwtConstants.xIndent, raw.y + 2 * SwtConstants.yIndent);
	}

	public static Point computeSizeForVerticallyStackedControl(int wHint, int hHint, Control... controls) {
		int sumY = 0;
		int maxX = 0;
		for (Control control : controls) {
			Point size = control.computeSize(wHint, hHint);
			maxX = Math.max(maxX, size.x);
			sumY += size.y;
		}
		return new Point(maxX, sumY);
	}

	public static Point computeSizeForHorizontallyStackedComposites(int wHint, int hHint, Control... controls) {
		int sumX = 0;
		int maxY = 0;
		for (Control control : controls) {
			Point size = control.computeSize(wHint, hHint);
			sumX += size.x;
			maxY = Math.max(maxY, size.y);
		}
		return new Point(sumX, maxY);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Control> T getDescendant(Control control, int... childIndicies) {
		return (T) getDescendant(control, 0, childIndicies);

	}

	public static Control getDescendant(Control control, int index, int[] childIndicies) {
		if (index >= childIndicies.length)
			return control;
		if (control instanceof Composite)
			return getDescendant(((Composite) control).getChildren()[childIndicies[index]], index + 1, childIndicies);
		throw new IllegalArgumentException(MessageFormat.format(UtilityMessages.cannotGetDescendant, control, index, ArrayHelper.asList(childIndicies)));
	}

	public static void checkColumns(Table table, String... expected) {
		for (int index = 0; index < expected.length; index++)
			Assert.assertEquals("index: " + index, expected[index], table.getColumn(index).getText());
		Assert.assertEquals(expected.length, table.getColumnCount());
	}

	public static void checkRow(Table table, int i, String... expected) {
		TableItem item = table.getItem(i);
		for (int index = 0; index < expected.length; index++)
			Assert.assertEquals("index: " + index, expected[index], item.getText(index));
	}

	/** the table must have two columns, and the tableItem.getData holds keys */
	public static String getStringFor(Table table, String key) {
		if (table.getColumnCount() != 2)
			throw new IllegalArgumentException(Integer.toString(table.getColumnCount()));
		for (TableItem item : table.getItems())
			if (key.equals(item.getData()))
				return Strings.nullSafeToString(item.getText(1));
		throw new IllegalArgumentException(key);
	}

	public static void setText(Control control, String newValue) {
		if (control instanceof Text)
			((Text) control).setText(newValue);
		else if (control instanceof StyledText)
			((StyledText) control).setText(newValue);
		else
			throw new IllegalArgumentException();

	}

	public static void packTables(Table... tables) {
		packColumns(tables);
		for (Table table : tables)
			table.pack();
	}

	public static void packColumns(Table... tables) {
		for (Table table : tables)
			for (TableColumn column : table.getColumns())
				column.pack();
	}

	public static void setChildrenBackgroundToMatch(Composite composite) {
		Color background = composite.getBackground();
		setChildrenBackgroundTo(composite, background);
	}

	public static void setChildrenBackgroundTo(Composite composite, Color background) {
		for (Control control : composite.getChildren())
			control.setBackground(background);
	}

	public static boolean isAfter(Control text1, Control text2) {
		Composite parent1 = text1.getParent();
		Composite parent2 = text2.getParent();
		if (parent1 == parent2) {
			int index1 = ArrayHelper.indentityIndexOf(parent1.getChildren(), text1);
			int index2 = ArrayHelper.indentityIndexOf(parent1.getChildren(), text2);
			assert index1 != -1 && index2 != -1 : "index1: " + index1 + "index 2 ";
			return index1 > index2;
		}
		return false;
	}

	public static boolean isBefore(Control text1, Control text2) {
		Composite parent1 = text1.getParent();
		Composite parent2 = text2.getParent();
		if (parent1 == parent2) {
			int index1 = ArrayHelper.indentityIndexOf(parent1.getChildren(), text1);
			int index2 = ArrayHelper.indentityIndexOf(parent1.getChildren(), text2);
			assert index1 != -1 && index2 != -1 : "index1: " + index1 + "index 2 ";
			return index1 < index2;
		}
		return false;
	}

	public static void selectOnlyAndNotifyListener(Table table, int i) {
		table.deselectAll();
		table.select(i);
		table.notifyListeners(SWT.Selection, new Event());

	}

	public static void selectAndNotifyListener(Table table, int... is) {
		table.deselectAll();
		for (int i : is)
			table.select(i);
		table.notifyListeners(SWT.Selection, new Event());

	}

	public static void checkTextMatches(IHasComposite values, String... expected) {
		checkTextMatches(values.getComposite(), expected);
	}

	public static void checkTextMatches(Composite values, String... expected) {
		Control[] children = values.getChildren();
		for (int i = 0; i < expected.length; i++) {
			Control control = children[i * 2 + 1];
			if (control instanceof Text)
				Assert.assertEquals(expected[i], ((Text) control).getText());
			else if (control instanceof StyledText)
				Assert.assertEquals(expected[i], ((StyledText) control).getText());
			else
				throw new IllegalArgumentException(control.getClass().getName());
		}
	}

	public static void checkLabelsMatch(IHasComposite editor, String... expected) {
		checkLabelsMatch(editor.getComposite(), expected);
	}

	public static void checkLabelsMatch(Composite editor, String... expected) {
		Control[] children = editor.getChildren();
		Assert.assertEquals(expected.length * 2, children.length);
		for (int i = 0; i < expected.length; i++) {
			Label label = (Label) children[i * 2];
			Assert.assertEquals(expected[i], label.getText());
		}
	}

	public static void select(Table table, int colIndex, String value) {
		for (int i = 0; i < table.getItemCount(); i++) {
			String text = table.getItem(i).getText(colIndex);
			if (text.equals(value)) {
				table.select(i);
				return;
			}
		}
		throw new IllegalArgumentException(colIndex + ", " + value);
	}

	public static void checkCombo(Composite composite, int index, List<String> items, String text) {
		Combo combo = (Combo) composite.getChildren()[index];
		List<String> actual = Arrays.asList(combo.getItems());
		Assert.assertEquals(items, actual);
		Assert.assertEquals(text, combo.getText());

	}

	public static void checkRadioButton(Composite composite, int index, String text, boolean selected) {
		Button button = (Button) composite.getChildren()[index];
		Assert.assertEquals(text, button.getText());
		Assert.assertEquals(selected, button.getSelection());
	}

	public static void checkTableColumns(Table table, String... strings) {
		Assert.assertTrue(table.getHeaderVisible());
		for (int i = 0; i < strings.length; i++)
			Assert.assertEquals(strings[i], table.getColumn(i).getText());
		Assert.assertEquals(strings.length, table.getColumnCount());

	}

	public static void checkTable(Table table, int index, Object key, String... strings) {
		TableItem item = table.getItem(index);
		TableColumn[] columns = table.getColumns();
		Assert.assertEquals(key, item.getData());
		for (int i = 0; i < strings.length; i++)
			Assert.assertEquals(strings[i], item.getText(i));
		Assert.assertEquals(columns.length, strings.length);

	}

	public static void addItemToStartOfCombo(Combo combo, String value, int maxSize) {
		combo.setText(value);
		String[] items = combo.getItems();
		boolean found = false;
		for (int i = 0; i < items.length && !found; i++) {
			if (items[i].equals(value))
				found = true;
		}
		if (!found) {
			if (items.length >= maxSize) {
				System.arraycopy(items, 0, items, 1, items.length - 1);
				items[0] = value;
				combo.setItems(items);
			} else {
				combo.add(value, 0);
			}
		}
	}

	public static Label createMigLabel(Composite parent, String text, String constraint) {
		Label result = new Label(parent, SWT.NULL);
		result.setLayoutData(constraint);
		result.setText(text);
		return result;
	}

	public static Text createMigText(Composite parent, String text, String constraint) {
		Text result = new Text(parent, SWT.NULL);
		result.setLayoutData(constraint);
		result.setText(text);
		return result;
	}

	public static StyledText createMigStyledText(Composite parent, int style, String text, String constraint) {
		StyledText result = new StyledText(parent, style);
		result.setLayoutData(constraint);
		result.setText(text);
		return result;
	}

	public static org.eclipse.swt.widgets.List createMigList(Composite parent, int style, String constraint) {
		org.eclipse.swt.widgets.List result = new org.eclipse.swt.widgets.List(parent, style);
		result.setLayoutData(constraint);
		return result;
	}

	public static Text createMigLabelAndTextForForm(Composite composite, String label, String text) {
		createMigLabel(composite, label, "");
		return Swts.createMigText(composite, text, "growx,wrap");

	}

	public static TabFolder createMigTab(Composite parent, int style, String constraints) {
		TabFolder result = new TabFolder(parent, style);
		result.setLayoutData(constraints);
		return result;

	}

	public static TabItem createTabItem(TabFolder tabFolder, int style, String text, Control control) {
		TabItem result = new TabItem(tabFolder, style);
		result.setText(text);
		result.setControl(control);
		return result;
	}

	public static ToolBar createMigToolbar(Composite parent, int style, String constraint) {
		ToolBar toolBar = new ToolBar(parent, style);
		toolBar.setLayoutData(constraint);
		return toolBar;
	}

	public static Combo createMigCombo(Composite parent, int style, String constraint) {
		Combo result = new Combo(parent, style);
		result.setLayoutData(constraint);
		return result;
	}

	public static Browser createMigBrowser(Composite parent, int style, String constraint) {
		Browser browser = new Browser(parent, style);
		browser.setLayoutData(constraint);
		return browser;
	}

	public static Composite createMigComposite(Composite parent,  int style, MigLayout layout,String constraint) {
		Composite composite = new Composite(parent, style);
		composite.setLayout(layout);
		composite.setLayoutData(constraint);
		return composite;
	}

	public static ToolItem createPushToolItem(ToolBar toolbar, ImageRegistry imageRegistry, String imageKey, Listener listener) {
		ToolItem result = new ToolItem(toolbar, SWT.PUSH);
		result.setImage(imageRegistry.get(imageKey));
		result.addListener(SWT.Selection, listener);
		return result;
	}

}