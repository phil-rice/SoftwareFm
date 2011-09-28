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
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class SituationListAnd implements IHasComposite {
	private final Composite content;
	private List situationList;
	private ISituationDisplayer situationDisplayer;

	public SituationListAnd(Composite parent, Callable<? extends Iterable<String>> situations, IFunction1<Composite, ISituationDisplayer> childWindowCreator) {
		this.content = new Composite(parent, SWT.NULL);
		try {
			content.setLayout(new GridLayout(2, true));
			situationList = new List(content, SWT.NULL);
			Iterable<String> list = situations.call();
			for (String object : list)
				situationList.add(object);
			situationDisplayer = childWindowCreator.apply(content);
			Control control = situationDisplayer.getControl();
			control.setLayoutData(Swts.makeGrabHorizonalAndFillGridData());
			SelectionAdapter listener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateSituation(situationList, situationDisplayer);
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
		updateSituation(situationList, situationDisplayer);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	private void updateSituation(final List situationList, final ISituationDisplayer situationDisplayer) {
		try {
			int index = situationList.getSelectionIndex();
			String item = index == -1 ? null : situationList.getItem(index);
			situationDisplayer.itemSelected(item);
		} catch (Exception e1) {
			throw WrappedException.wrap(e1);
		}
	}

}
