package org.softwareFm.display.editor;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.IHasRightHandSide;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class EditorFactory implements IEditorFactory, ISimpleMap<String, IEditor> {

	private final Map<String, IEditor> map = Maps.newMap(LinkedHashMap.class);
	private IEditor editor;
	private final EditorContext editorContext;
	private Control rememberedControl;

	public EditorFactory(EditorContext editorContext) {
		this.editorContext = editorContext;
	}

	@Override
	public void displayEditor(Shell parent, String editorName, DisplayerDefn displayerDefn,  final ActionContext actionContext, ActionData actionData, final ICallback<Object> onCompletion, Object initialValue) {
		if (editor != null)
			editor.cancel();
		final IHasRightHandSide rightHandSide = actionContext.rightHandSide;
		rememberedControl = rightHandSide.getVisibleControl();
		Swts.layoutDump(rememberedControl);
		editor = get(editorName);
		if (editor.getControl() == null)
			editor.createControl(actionContext);
		rightHandSide.makeVisible(editor.getControl());
		editor.edit(parent, displayerDefn, editorContext, actionContext, actionData, new IEditorCompletion() {
			@Override
			public void ok(Object value) {
				editor = null;
				rightHandSide.makeVisible(rememberedControl);
				ICallback.Utils.processWithWrap(onCompletion, value);
			}
			
			@Override
			public void cancel() {
				editor = null;
				rightHandSide.makeVisible(rememberedControl);
			}
		}, initialValue);

	}

	public IEditor getEditor() {
		return editor;
	}

	@Override
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
	
	@Override
	public void cancel(){
		if (editor != null){
			editor.cancel();
			editor = null;
		}
	}

}
