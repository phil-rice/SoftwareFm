package org.softwareFm.display.swt;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.indent.Indent;
import org.softwareFm.utilities.resources.IResourceGetter;

public class Swts {

	public static Composite newComposite(Composite parent, int style, final String description) {
		return new Composite(parent, style) {
			@Override
			public String toString() {
				return description + "." + super.toString();
			}
		};
	}

	public static Sash newSash(Composite parent, int style, final String description) {
		return new Sash(parent, style) {
			@Override
			public String toString() {
				return description + "." + super.toString();
			}
		};
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

	public static void addGrabHorizontalAndFillGridDataToAllChildren(Composite composite) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		for (Control control : composite.getChildren()) {
			GridData data = makeGrabHorizonalAndFillGridData();
			control.setLayoutData(data);
		}
	}
	public static void addGrabHorizontalAndFillGridDataToAllChildrenWithHeightHint(Composite composite, int heightHint) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		for (Control control : composite.getChildren()) {
			GridData data = makeGrabHorizonalAndFillGridDataWithHeight(heightHint);
			control.setLayoutData(data);
		}
	}

	public static Composite makeAcceptCancelComposite(Composite parent, int style, IResourceGetter resourceGetter, final Runnable onAccept, final Runnable onCancel) {
		Composite result = new Composite(parent, style);
		result.setLayout(new GridLayout(2, true));
		Button cancelButton = new Button(result, SWT.PUSH);
		cancelButton.setText(IResourceGetter.Utils.getOrException(resourceGetter, "button.cancel.title"));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onCancel.run();
			}
		});
		Button okButton = new Button(result, SWT.PUSH);
		okButton.setText(IResourceGetter.Utils.getOrException(resourceGetter, "button.ok.title"));
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onAccept.run();
			}
		});
		return result;
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

	public static Layout getHorizonalMarginRowLayout(int margin) {
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

	public static Layout getHorizonalNoMarginRowLayout() {
		return getHorizonalMarginRowLayout(0);
	}

	public static GridData makeGrabHorizonalAndFillGridDataWithHeight(int heightHint) {
		GridData result = makeGrabHorizonalAndFillGridData();
		result.heightHint = heightHint;
		return result;

	}

	public static GridData makeGrabHorizonalAndFillGridData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		return data;
	}

	public static GridData makeGrabHorizonalVerticalAndFillGridData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		return data;
	}

	public static String layoutAsString(Control control) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(control);
		Point size = control.getSize();
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
		System.out.println(indent + control.getClass().getSimpleName() + "(Visible: " + control.isVisible() + ": " + layoutAsString(control));
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			for (Control nested : composite.getChildren()) {
				Indent indented = indent.indent();
				layoutDump(nested, indented);
			}
		}
	}

	public static void display(String title, IFunction1<Composite, Composite> builder) {
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setSize(600, 400);
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

	public static <T extends IHasControl> void xUnit(String title, final File root, final String extension, final ISituationListAndBuilder<T> builder) {
		Swts.display(title, new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Callable<? extends Iterable<String>> situations = new Callable<Iterable<String>>() {
					@Override
					public Iterable<String> call() throws Exception {
						return Iterables.map(Files.walkChildrenOf(root, Files.extensionFilter(extension)), Files.toFileName());
					}
				};
				final SituationListAnd<T> result = new SituationListAnd<T>(from, situations, builder);
				result.addListener(new ISituationListListener<T>() {
					@Override
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

	public static void removeAllChildren(Composite composite) {
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

	public static <C extends Control> void setRowDataFor(int width, int height, C... controls) {
		for (Control control : controls)
			control.setLayoutData(new RowData(width, height));

	}

	public static void makeButtonFromMainMethod(Composite composite, final Class<?> classWithMain) {
		Button button = new Button(composite, SWT.PUSH);
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

	public static Layout getGridLayoutWithoutMargins(int columns) {
		GridLayout gridLayout = new GridLayout(columns, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		return gridLayout;
	}

	public static void asyncExec(IHasControl hasControl, Runnable runnable) {
		Control control = hasControl.getControl();
		if (!control.isDisposed())
			control.getDisplay().asyncExec(runnable);
	}

	public static void syncExec(IHasControl hasControl, Runnable runnable) {
		Control control = hasControl.getControl();
		if (!control.isDisposed())
			control.getDisplay().syncExec(runnable);
	}

	public static Button makePushButton(Composite parent, IResourceGetter resourceGetter, String titleKey, final Runnable runnable) {
		return makePushButton(parent, resourceGetter, titleKey, true, runnable);
	}

	public static Button makePushButton(Composite parent, IResourceGetter resourceGetter, String titleOrKey, boolean titleIsKey, final Runnable runnable) {
		Button button = new Button(parent, SWT.PUSH);
		String title = titleIsKey ? IResourceGetter.Utils.getOrException(resourceGetter, titleOrKey) : titleOrKey;
		button.setText(title);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runnable.run();
			}
		});
		return button;
	}

	public static void dispatchUntilQueueEmpty(Display display) {
		while (display.readAndDispatch())
			;
	}

}
