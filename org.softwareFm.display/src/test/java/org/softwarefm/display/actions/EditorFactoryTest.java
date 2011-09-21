package org.softwareFm.display.actions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.AbstractSimpleMapTest;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class EditorFactoryTest extends AbstractSimpleMapTest<String, IEditor> {

	private Shell shell;
	private List<String> formalParams1 = Arrays.asList("fp1");
	private List<String> formalParams2 = Arrays.asList("fp2");
	private List<Object> actualParams1 = Arrays.<Object>asList("ap1");
	private List<Object> actualParams2 = Arrays.<Object>asList("ap2");
	private ActionData actionData1 = new ActionData(Maps.<String,String>newMap(), formalParams1, actualParams1);
	private ActionData actionData2 = new ActionData(Maps.<String,String>newMap(), formalParams2, actualParams2);
	private EditorContext editorContext;
	ActionContext actionContext = null;

	@SuppressWarnings("unchecked")
	public void testOpeningEditor() {
		EditorMock editorMock1 = new EditorMock("1");
		EditorFactory factory = new EditorFactory(editorContext).register("name1", editorMock1);
		factory.displayEditor(shell, "name1", actionContext, actionData1, ICallback.Utils.exception("shouldn't close"));
		assertEquals(Arrays.asList(formalParams1), editorMock1.formalParams);
		assertEquals(Arrays.asList(actualParams1), editorMock1.actualParams);
		assertEquals(Arrays.asList(shell), editorMock1.parents);
		assertEquals(0, editorMock1.cancelCount.get());
		assertSame(editorMock1, factory.getEditor());
	}

	public void testOpeningSecondEditorCancelsFirst() {
		EditorMock editorMock1 = new EditorMock("1");
		EditorMock editorMock2 = new EditorMock("2");
		EditorFactory factory = new EditorFactory(editorContext).register("name1", editorMock1).register("name2", editorMock2);
		factory.displayEditor(shell, "name1",actionContext,  actionData1, ICallback.Utils.exception("shouldn't close"));
		assertEquals(0, editorMock1.cancelCount.get());
		factory.displayEditor(shell, "name2", actionContext,  actionData2, ICallback.Utils.exception("shouldn't close"));
		assertEquals(1, editorMock1.cancelCount.get());
		assertEquals(0, editorMock2.cancelCount.get());
	}

	public void testEditorCloseCallsOnCompletion() {
		EditorMock editorMock1 = new EditorMock("1");
		EditorFactory factory = new EditorFactory(editorContext).register("name1", editorMock1);
		final AtomicReference<Object> ref = new AtomicReference<Object>();
		factory.displayEditor(shell, "name1",  actionContext, actionData1, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				ref.set(t);
			}
		});
		assertNull(ref.get());
		assertSame(editorMock1, factory.getEditor());
		editorMock1.finish("finalValue");
		assertNull(factory.getEditor());
		assertEquals("finalValue", ref.get());
	}

	public void testThrowsExceptionIfKeyNotThere() {
		checkThrowsExceptionIfKeyNotThere();
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
		return new EditorFactory(editorContext);
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
		ImageRegistry imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(shell.getDisplay(), imageRegistry);
		
		CompositeConfig compositeConfig = new CompositeConfig(shell.getDisplay(), new SoftwareFmLayout(), imageRegistry, IResourceGetter.Utils.noResources());
		editorContext = new EditorContext(compositeConfig);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		shell.dispose();
		super.tearDown();
	}
}
