package org.softwareFm.display.editor;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.IHasRightHandSide;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.EditorIds;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.displayer.RippedEditorId;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class EditorFactory implements IEditorFactory, ISimpleMap<String, IEditor> {

	private final Map<String, IEditor> map = Maps.newMap(LinkedHashMap.class);
	private IEditor editor;
	private Control rememberedControl;
	private IHasRightHandSide rightHandSide;
	@Override
	public void displayEditor(final ActionContext actionContext, final DisplayerDefn displayerDefn, IDisplayer displayer) {
		try {
			if (editor != null)
				cancel();
			if (displayerDefn.editorId==null)
				return;
			rightHandSide = actionContext.rightHandSide;
			rememberedControl = rightHandSide.getVisibleControl();
			editor = get(displayerDefn.editorId);
			if (editor.getControl() == null)
				editor.createControl(actionContext);
			rightHandSide.makeVisible(editor.getControl());
			IButtonParent actionButtonParent = editor.actionButtonParent();
			Swts.removeAllChildren(actionButtonParent.getButtonComposite());
			displayerDefn.createButtons(actionButtonParent, actionContext, displayer);
			String dataKey = displayerDefn.dataKey;
			final RippedEditorId rip = EditorIds.rip(dataKey);
			IFunction1<String, String> entityToUrlGetter = actionContext.entityToUrlGetter;
			final String url = rip.entity==null?null:entityToUrlGetter.apply(rip.entity);
			final IEditorCompletion completion = new IEditorCompletion() {
				@Override
				public void ok(Map<String, Object> value) {
					editor = null;
					rightHandSide.makeVisible(rememberedControl);
					actionContext.updateStore.update(rip.entity, url, value);
				}
				
				@Override
				public void cancel() {
					editor = null;
					rightHandSide.makeVisible(rememberedControl);
				}
			};
			
			editor.edit(displayer, displayerDefn, actionContext, completion);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
		
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
			rightHandSide.makeVisible(rememberedControl);
			rememberedControl = null;
			editor = null;
		}
	}

}
