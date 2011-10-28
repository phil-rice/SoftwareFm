package org.softwareFm.display.impl;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.IHasRightHandSide;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionMock;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.actions.NoOperationAction;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.DataGetterMock;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.smallButtons.SimpleImageControl;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.tests.IIntegrationTest;

public abstract class AbstractDisplayerEditorIntegrationTest<D extends IDisplayer, E extends IEditor> extends TestCase implements IIntegrationTest {

	protected Shell shell;
	protected ActionMock actionMock;
	protected ActionStore actionStore;
	protected E editor;
	protected IHasRightHandSide rightHandSide;
	protected DisplayerDefn displayerDefn;
	protected ImageRegistry imageRegistry;
	protected IResourceGetter resourceGetter;

	abstract protected E makeEditor();

	abstract protected IResourceGetter makeResources();

	abstract protected DisplayerDefn makeBaseDisplayDefn();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = new Shell();
		actionMock = new ActionMock("anAction");
		actionStore = new ActionStore().action("someId", actionMock).action("action.no.operation", new NoOperationAction());
		imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(shell.getDisplay(), imageRegistry);
		resourceGetter = IResourceGetter.Utils.noResources().//
				with(new ResourceGetterMock(DisplayConstants.buttonCancelTitle, "cancelValue", DisplayConstants.buttonOkTitle, "okValue")).//
				with(makeResources());
		editor = makeEditor();
		rightHandSide = IHasRightHandSide.Utils.makeRightHandSide(shell);
		displayerDefn = makeBaseDisplayDefn();
	}

	protected IDisplayer makeAndPopulateDisplayer(DataGetterMock dataGetter) {
		ActionContext actionContext = makeActionContext(dataGetter);
		IDisplayer displayer = displayerDefn.createDisplayer(shell, actionContext);
		displayerDefn.data(actionContext, displayerDefn, displayer, "entity", "url");
		return displayer;
	}

	protected void clickOnEditor(IDisplayer displayer) {
		Control[] children = displayer.getButtonComposite().getChildren();
		SimpleImageControl control = (SimpleImageControl) children[0];
		control.notifyListeners(SWT.MouseDown, new Event());
	}

	protected ActionContext makeActionContext(IDataGetter dataGetter) {
		return makeActionContext(dataGetter, null);
	}

	protected ActionContext makeActionContext(IDataGetter dataGetter, IUpdateStore updateStore) {
		IFunction1<String, String> entityToUrl = new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return "UrlFor" + from;
			}
		};
		IEditorFactory editorFactory = new EditorFactory().register("someEditor", editor);
		return new ActionContext(rightHandSide, actionStore, dataGetter, entityToUrl, new CompositeConfig(shell.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter), editorFactory, updateStore, null, null);
	}

	@Override
	protected void tearDown() throws Exception {
		shell.dispose();
		super.tearDown();
	}
}
