package org.softwareFm.display.editor;

import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.Swts;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class Editors {

	public static void display(String title, final IEditor editor, final List<String> formalParameters, final List<Object>actualParameters, final Object initialValue) {
		Swts.display(title, new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
				final EditorContext editorContext = new EditorContext(new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter));
				final IEditorFactory editorFactory = new EditorFactory(editorContext).register("someName", editor);
				Button button = new Button(composite, SWT.PUSH);
				button.setText("Edit");
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Shell shell = from.getShell();
						ICallback<Object> onCompletion = new ICallback<Object>(){
							@Override
							public void process(Object t) throws Exception {
								System.out.println("Result: " + t);
							}};
							ActionData actionData = new ActionData(Maps.<String,String>newMap(), formalParameters, actualParameters);
						editorFactory.displayEditor(shell, "someName",null, null, actionData, onCompletion, initialValue);
					}
				});
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}
		});

	}

}
