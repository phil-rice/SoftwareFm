package org.softwareFm.display.swt;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.rss.ISituationListCallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class SituationListAnd <T extends IHasControl>implements IHasComposite {
	private final Composite content;
	private List situationList;
	private T situationDisplayer;
	private final ISituationListCallback<T> callback;

	public SituationListAnd(Composite parent, Callable<? extends Iterable<String>> situations, IFunction1<Composite, T> childWindowCreator, ISituationListCallback<T> callback) {
		this.callback = callback;
		this.content = new Composite(parent, SWT.NULL);
		try {
			content.setLayout(new GridLayout(2, true));
			situationList = new List(content, SWT.NULL);
			Iterable<String> list = situations.call();
			for (String object : list)
				situationList.add(object);
			situationDisplayer = childWindowCreator.apply(content);
			Control control = situationDisplayer.getControl();
			control.setLayoutData(Swts.makeGrabHorizonalVerticalAndFillGridData());
			SelectionAdapter listener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateSituation();
				}
			};
			situationList.addSelectionListener(listener);
			situationList.setLayoutData(Swts.makeGrabHorizonalVerticalAndFillGridData());

		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}


	public void selectFirst() {
		situationList.select(0);
		updateSituation();
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	private void updateSituation() {
		try {
			int index = situationList.getSelectionIndex();
			String item = index == -1 ? null : situationList.getItem(index);
			callback.selected(situationDisplayer, item);
		} catch (Exception e1) {
			throw WrappedException.wrap(e1);
		}
	}

}
