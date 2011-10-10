package org.softwareFm.display.actions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.IHasRightHandSide;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAnd;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.display.editor.RememberUpdateStore;
import org.softwareFm.display.impl.EntityToUrlMock;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.maps.AbstractSimpleMapTest;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class EditorFactoryTest extends AbstractSimpleMapTest<String, IEditor> {

	private Shell shell;
	private final List<String> formalParams1 = Arrays.asList("fp1");
	private final List<String> formalParams2 = Arrays.asList("fp2");
	private final List<Object> actualParams1 = Arrays.<Object> asList("ap1");
	private final List<Object> actualParams2 = Arrays.<Object> asList("ap2");
	private final ActionData actionData1 = new ActionData(Maps.<String, String> newMap(), formalParams1, actualParams1);
	private final ActionData actionData2 = new ActionData(Maps.<String, String> newMap(), formalParams2, actualParams2);
	ActionContext actionContext = null;
	private DisplayerDefn displayerDefn1;
	private Label original;
	private TitleAnd displayer;
	private IHasRightHandSide rightHandSide;
	private DisplayerDefn displayerDefn2;
	private EditorMock editorMock1;
	private EditorMock editorMock2;
	private EditorFactory editorFactory;
	private RememberUpdateStore updateStore;

	public void testOpeningEditor() {
		editorFactory.displayEditor(actionContext, displayerDefn1, displayer);

		assertEquals(Arrays.asList(displayer), editorMock1.parents);
		assertEquals(Arrays.asList(actionContext), editorMock1.actionsContexts);
		assertSame(editorMock1, editorFactory.getEditor());
		assertEquals(editorMock1.getControl(), rightHandSide.getVisibleControl());
	}

	public void testOpeningSecondEditorMakesItVisible() {
		editorFactory.displayEditor(actionContext, displayerDefn1, displayer);
		editorFactory.displayEditor(actionContext, displayerDefn2, displayer);

		assertEquals(editorMock2.getControl(), rightHandSide.getVisibleControl());

	}
	public void testOpeningSecondEditorDoesntUpdateStore() {
		editorFactory.displayEditor(actionContext, displayerDefn1, displayer);
		editorFactory.displayEditor(actionContext, displayerDefn2, displayer);
		
		assertEquals(editorMock2.getControl(), rightHandSide.getVisibleControl());
		assertEquals(Collections.emptyList(), updateStore.datas);
	}

	public void testCancelDoesntUpdateStore(){
		editorFactory.displayEditor(actionContext, displayerDefn1, displayer);
		editorFactory.cancel();
		
		assertEquals(Collections.emptyList(), updateStore.datas);
		
	}
	
	public void testCancelOnSecondEditorReplacesOriginal() {
		editorFactory.displayEditor(actionContext, displayerDefn1, displayer);
		editorFactory.displayEditor(actionContext, displayerDefn2, displayer);
		editorFactory.cancel();
		assertEquals(original, rightHandSide.getVisibleControl());
	}

	public void testFinishOnSecondEditorReplacesOriginal() {
		editorFactory.displayEditor(actionContext, displayerDefn1, displayer);
		editorFactory.displayEditor(actionContext, displayerDefn2, displayer);
		editorMock2.finish();
		assertEquals(original, rightHandSide.getVisibleControl());
	}

	@SuppressWarnings("unchecked")
	public void testFinishSendsData(){
		editorFactory.displayEditor(actionContext, displayerDefn1, displayer);
		editorFactory.displayEditor(actionContext, displayerDefn2, displayer);
		editorMock2.finish();
		assertEquals(original, rightHandSide.getVisibleControl());
		
		assertEquals(Arrays.asList("entity"), updateStore.entities);
		assertEquals(Arrays.asList("lastUrlForEntity"), updateStore.urls);
		assertEquals(Arrays.asList(EditorMock.finishedData), updateStore.datas);
		
	}
	
	

	public void testThrowsExceptionIfKeyNotThere() {
		checkThrowsExceptionIfKeyNotThere("Map doesn't have key b. Legal keys are [a]. Map is {a=EditorMock [seed=1]}");
	}

	@Override
	protected String makeKey(String seed) {
		return seed;
	}

	@Override
	protected IEditor makeValue(String seed) {
		return new EditorMock(seed);
	}

	@Override
	protected ISimpleMap<String, IEditor> blankMap() {
		return new EditorFactory();
	}

	@Override
	protected String duplicateKeyErrorMessage() {
		return "Cannot set value of editor twice. Current value [EditorMock [seed=1]]. New value [EditorMock [seed=2]]";
	}

	@Override
	protected void put(ISimpleMap<String, IEditor> map, String key, IEditor value) {
		assertSame(map, ((EditorFactory) map).register(key, value));
	}

	@Override
	protected void setUp() throws Exception {
		shell = new Shell();

		displayerDefn1 = new DisplayerDefn(null).editor("mockEditor1").data("data.entity.key1");
		displayerDefn2 = new DisplayerDefn(null).editor("mockEditor2").data("data.entity.key2");
		editorMock1 = new EditorMock("1");
		editorMock2 = new EditorMock("2");
		editorFactory = new EditorFactory().register(displayerDefn1.editorId, editorMock1).register(displayerDefn2.editorId, editorMock2);

		ImageRegistry imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(shell.getDisplay(), imageRegistry);

		CompositeConfig compositeConfig = new CompositeConfig(shell.getDisplay(), new SoftwareFmLayout(), imageRegistry, IResourceGetter.Utils.noResources());
		displayer = new TitleAnd(compositeConfig, shell, "", false);
		final Composite rightHandSideComposite = Swts.newComposite(shell, SWT.NULL, "RHS Composite");
		rightHandSide = IHasRightHandSide.Utils.makeRightHandSide(shell);
		original = new Label(rightHandSideComposite, SWT.NULL);
		original.setText("original");
		rightHandSide.makeVisible(original);
		updateStore = IUpdateStore.Utils.rememberUpdateStore();
		EntityToUrlMock entityToUrl = new EntityToUrlMock("entity", "lastUrlForEntity");
		actionContext = new ActionContext(rightHandSide, null, null, entityToUrl, compositeConfig, null, updateStore, null, null);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		shell.dispose();
		super.tearDown();
	}
}
