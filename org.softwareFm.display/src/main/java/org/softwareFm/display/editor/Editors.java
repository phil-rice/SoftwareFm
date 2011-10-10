package org.softwareFm.display.editor;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.IHasRightHandSide;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.CompressedText;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class Editors {

	public static void oneDisplay(String title, final IEditor editor, final Object initialValue) {
		display(title, editor, "data.entity.someName", initialValue);
	}

	public static void display(String title, final IEditor editor, final Object... nameAndInitialValues) {
		Swts.display(title, new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				SashForm holder = new SashForm(from, SWT.NULL);
				holder.setLayout(new FillLayout());
				Composite displayerParent = Swts.newComposite(holder, SWT.NULL, "leftHandSide");
				displayerParent.setLayout(new FillLayout());
				final IHasRightHandSide rightHandSide = IHasRightHandSide.Utils.makeRightHandSide(holder);

				final ActionContext actionContext = makeActionContext(holder, rightHandSide, nameAndInitialValues);

				Label label = new Label(rightHandSide.getComposite(), SWT.NULL);
				label.setText("Background");
				for (int i = 0; i < nameAndInitialValues.length; i += 2) {
					final String name = (String) nameAndInitialValues[i + 0];
					actionContext.editorFactory.register(name, editor);
					final DisplayerDefn defn = new DisplayerDefn(null).editor(name).data((String) nameAndInitialValues[i + 0]).title("title.title");
					final CompressedText displayer = new CompressedText(displayerParent, SWT.NULL, actionContext.compositeConfig);
					displayer.setTitle(name);
					displayer.setText(Strings.nullSafeToString(nameAndInitialValues[i + 1]));
					displayer.addClickListener(new Listener() {
						@Override
						public void handleEvent(Event event) {
							actionContext.editorFactory.displayEditor(actionContext, defn, displayer);
						}
					});
				}

				Swts.addGrabHorizontalAndFillGridDataToAllChildren(displayerParent);
				holder.setWeights(new int[] { 1, 2 });
				return holder;
			}
		});

	}

	private static ActionContext makeActionContext(final Composite from, IHasRightHandSide rightHandSide, final Object... nameAndInitialValues) {
		IDataGetter dataGetter = new DataGetterMock(nameAndInitialValues);
		ImageRegistry imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
		IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
		final CompositeConfig compositeConfig = new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter);
		final IEditorFactory editorFactory = new EditorFactory();
		IUpdateStore updateStore = IUpdateStore.Utils.sysoutUpdateStore();
		IFunction1<String, String> entityToUrlGetter = new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return "UrlFor" + from;
			}
		};
		ActionContext actionContext = new ActionContext(rightHandSide, new ActionStore(), dataGetter, entityToUrlGetter, compositeConfig, editorFactory, updateStore, null, ICallback.Utils.rethrow());
		return actionContext;
	}

}
