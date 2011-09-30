package org.softwareFm.display.swt;

import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.exceptions.WrappedException;

public class SituationListAnd<T extends IHasControl> implements IHasComposite {
	private final Composite content;
	private List situationList;
	private T situationDisplayer;

	CopyOnWriteArrayList<ISituationListListener<T>> listeners = new CopyOnWriteArrayList<ISituationListListener<T>>();
	private StyledText situationContents;

	public SituationListAnd(Composite parent, Callable<? extends Iterable<String>> situations, ISituationListAndBuilder<T> builder) {
		this.content = new Composite(parent, SWT.NULL) {
			@Override
			public String toString() {
				return "situationListAnd.content" + super.toString();
			}
		};
		try {
			content.setLayout(new GridLayout(2, true));
			Composite leftColumn = new Composite(content, SWT.NULL);
			situationList = new List(leftColumn, SWT.NULL);
			situationContents = new StyledText(leftColumn, SWT.NULL);
			Swts.addGrabHorizontalAndFillGridDataToAllChildren(leftColumn);
			
			Iterable<String> list = situations.call();
			for (String object : list)
				situationList.add(object);
			situationDisplayer = builder.makeChild(content);
			Control control = situationDisplayer.getControl();
			situationList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					fire();
				}

			});
			leftColumn.setLayoutData(Swts.makeGrabHorizonalVerticalAndFillGridData());
			control.setLayoutData(Swts.makeGrabHorizonalVerticalAndFillGridData());

		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}


	public void setText(String text){
		situationContents.setText(text);
	}
	
	private void fire() {
		try {
			int index = situationList.getSelectionIndex();
			String value = index == -1 ? null : situationList.getItem(index);
			for (ISituationListListener<T> listener : listeners)
				listener.selected(situationDisplayer, value);
		} catch (Exception e1) {
			throw WrappedException.wrap(e1);
		}
	};

	public void selectFirst() {
		situationList.setSelection(0);
		fire();
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public void addListener(ISituationListListener<T> listener) {
		listeners.add(listener);

	}

}