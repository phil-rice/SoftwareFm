package org.softwarefm.display.editor;

import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.Sample;
import org.softwarefm.display.SoftwareFmLayout;
import org.softwarefm.display.composites.CompositeConfig;

public class Editors {

	public static void display(String title, final IEditor editor, final List<String> formalParameters, final List<Object>actualParameters) {
		Swts.display(title, new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(Sample.class, "SoftwareFmDisplay");
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
						editorFactory.displayEditor(shell, "someName",formalParameters, actualParameters, onCompletion);
					}
				});
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}
		});

	}

}
