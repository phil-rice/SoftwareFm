package org.softwareFm.display.displayer;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.composites.TitleAnd;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class ListDisplayer implements IDisplayer {

	private final CompositeConfig compositeConfig;
	private final Composite content;
	private final TitleAnd mainLine;
	private final Composite listComposite;
	private final ActionContext actionContext;

	public ListDisplayer(Composite parent, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionContext actionContext) {
		this.compositeConfig = compositeConfig;
		this.actionContext = actionContext;
		this.content = new Composite(parent, SWT.NULL);
		this.listComposite = new Composite(content, SWT.NULL);
		this.mainLine = new TitleAnd(compositeConfig, content, defn.title, true);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
	}

	@SuppressWarnings("unchecked")
	public void data(IDataGetter dataGetter, DisplayerDefn defn, String entity, String url) {
		Swts.removeAllChildren(listComposite);
		Object dataFor = dataGetter.getDataFor(defn.dataKey);
		if (dataFor != null) {
			if (!(dataFor instanceof List))
				throw new IllegalStateException(MessageFormat.format(DisplayConstants.expectedAList, dataFor, dataFor.getClass(), defn));
			List<String> dataList = (List<String>) dataFor;
			boolean actuallyEmpty = dataList.size() == 1 && dataList.get(0).trim().length() == 0;
			int index = 0;
			// if (!actuallyEmpty)
			// for (Object value : dataList) {
			// IButtonParent buttonParent = listEditor.makeLineHasControl(defn, compositeConfig, listComposite, index, value);
			// defn.createDefaultAction(actionContext, this, buttonParent, index);
			// index++;
			// }
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

	@Override
	public void addClickListener(Listener listener) {

	}

	public ListDisplayer(CompositeConfig compositeConfig, Composite content, TitleAnd mainLine, Composite listComposite, ActionContext actionContext) {
		super();
		this.compositeConfig = compositeConfig;
		this.content = content;
		this.mainLine = mainLine;
		this.listComposite = listComposite;
		this.actionContext = actionContext;
	}

	@Override
	public void highlight() {
	}

	@Override
	public void unhighlight() {
	}

}
