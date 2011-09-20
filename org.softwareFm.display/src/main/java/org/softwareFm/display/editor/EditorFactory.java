package org.softwareFm.display.editor;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.data.DisplayConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class EditorFactory implements IEditorFactory, ISimpleMap<String, IEditor> {

	private Map<String, IEditor> map = Maps.newMap(LinkedHashMap.class);
	private IEditor editor;
	private EditorContext editorContext;

	public EditorFactory(EditorContext editorContext) {
		this.editorContext = editorContext;
	}

	@Override
	public void displayEditor(Shell parent, String editorName, List<String> formalParameters, List<Object> actualParameters, final ICallback<Object> onCompletion) {
		if (editor != null)
			editor.cancel();
		editor = get(editorName);
		editor.edit(parent, editorContext, formalParameters, actualParameters, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				editor.cancel();
				editor = null;
				onCompletion.process(t);
			}
		});

	}

	public IEditor getEditor() {
		return editor;
	}

	public EditorFactory register(String name, IEditor editor) {
		if (map.containsKey(name))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "editor", map.get(name), editor));
		map.put(name, editor);
		return this;
	}

	@Override
	public IEditor get(String key) {
		return Maps.getOrException(map, key);
	}

	@Override
	public List<String> keys() {
		return Iterables.list(map.keySet());
	}

}
