package org.softwarefm.display.displayer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.actions.ActionContext;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.composites.TitleAnd;
import org.softwarefm.display.data.DisplayConstants;
import org.softwarefm.display.data.IDataGetter;
import org.softwarefm.display.impl.DisplayerDefn;
import org.softwarefm.display.lists.IListEditor;

public class ListDisplayer implements IDisplayer {

	private final CompositeConfig compositeConfig;
	private Composite content;
	private TitleAnd mainLine;
	private Composite listComposite;
	private IListEditor listEditor;
	private final ActionStore actionStore;
	private final ActionContext actionContext;

	public ListDisplayer(Composite parent, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		this.compositeConfig = compositeConfig;
		this.actionStore = actionStore;
		this.actionContext = actionContext;
		this.listEditor = defn.listEditor;
		if (listEditor == null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, "listEditor", defn));
		this.content = new Composite(parent, SWT.BORDER);
		this.mainLine = new TitleAnd(compositeConfig, content, defn.title, true);
		this.listComposite = new Composite(content, SWT.NULL);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
	}

	@SuppressWarnings("unchecked")
	public void data(IDataGetter dataGetter, DisplayerDefn defn, String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		Swts.removeAllChildren(listComposite);
		Object dataFor = dataGetter.getDataFor(defn.dataKey);
		if (dataFor != null) {
			if (!(dataFor instanceof List))
				throw new IllegalStateException(MessageFormat.format(DisplayConstants.expectedAList, dataFor, dataFor.getClass(), defn));
			List<Object> dataList = (List<Object>) dataFor;
			int index = 0;
			for (Object value : dataList) {
				IButtonParent buttonParent = listEditor.makeLineHasControl(defn, compositeConfig, listComposite, index, value);
				defn.createActions(actionStore, actionContext, this, buttonParent, defn.listActionDefns, index);
				index++;
			}
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(listComposite);
		
		content.getParent().getParent().layout();
		content.getParent().getParent().redraw();

		content.getParent().layout();
		content.getParent().redraw();

		listComposite.layout();
		listComposite.redraw();
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getButtonComposite() {
		return mainLine.getButtonComposite();
	}

	@Override
	public ImageRegistry getImageRegistry() {
		return compositeConfig.imageRegistry;
	}

	@Override
	public IResourceGetter getResourceGetter() {
		return compositeConfig.resourceGetter;
	}

	@Override
	public void buttonAdded(IHasControl button) {
		mainLine.buttonAdded(button);
	}

}
