package org.softwareFm.display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.display.data.DisplayConstants;
import org.softwareFm.display.impl.LargeButtonDefn;
import org.softwareFm.display.impl.SmallButtonDefn;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class DisplaySelectionModel {

	private String largeButtonSelectedId;
	private final Map<String, List<String>> smallButtonSelectionMap = Maps.newMap();
	private final CopyOnWriteArrayList<IDisplaySelectionListener> listeners = new CopyOnWriteArrayList<IDisplaySelectionListener>();
	private final ICallback<Throwable> exceptionHandler;
	private final List<LargeButtonDefn> largeButtonDefns;

	public DisplaySelectionModel(ICallback<Throwable> exceptionHandler, LargeButtonDefn... largeButtonDefns) {
		if (largeButtonDefns.length == 0)
			throw new IllegalArgumentException(DisplayConstants.mustHaveSomeLargeButtons);
		this.exceptionHandler = exceptionHandler;
		this.largeButtonDefns = new ArrayList<LargeButtonDefn>(Arrays.asList(largeButtonDefns));
		populateVisible(largeButtonDefns[0]);

	}

	private void populateVisible(final LargeButtonDefn largeButtonDefn) {
		this.largeButtonSelectedId = largeButtonDefn.id;
		selectedSmallButtonList(largeButtonDefn);

	}

	private List<String> selectedSmallButtonList(final LargeButtonDefn largeButtonDefn) {
		return Maps.findOrCreate(smallButtonSelectionMap, largeButtonDefn.id, new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return makeMasterSmallButtonList(largeButtonDefn);
			}

		});
	}

	private List<String> makeMasterSmallButtonList(final LargeButtonDefn largeButtonDefn) {
		return Iterables.list(Iterables.map(largeButtonDefn.defns, new IFunction1<SmallButtonDefn, String>() {
			@Override
			public String apply(SmallButtonDefn from) throws Exception {
				return from.id;
			}
		}));
	}

	public String getLargeButtonSelectedId() {
		return largeButtonSelectedId;
	}

	public void select(String smallButtonSelectionId) {
		for (LargeButtonDefn largeButtonDefn : largeButtonDefns)
			for (SmallButtonDefn smallButtonDefn : largeButtonDefn.defns)
				if (smallButtonDefn.id.equals(smallButtonSelectionId)) {
					if (largeButtonDefn.id.equals(largeButtonSelectedId)) {
						List<String> smallButtonList = selectedSmallButtonList(largeButtonDefn);
						if (smallButtonList.contains(smallButtonDefn.id))
							smallButtonList.remove(smallButtonDefn.id);
						else {
							smallButtonList.add(smallButtonDefn.id);
							Collections.sort(smallButtonList, Lists.byListOrder(makeMasterSmallButtonList(largeButtonDefn)));
						}
					} else
						populateVisible(largeButtonDefn);
					fire(largeButtonDefn, smallButtonDefn);
					return;
				}
	}

	public void addDisplaySelectionListener(IDisplaySelectionListener listener) {
		listeners.add(listener);
	}

	public void fire(LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn) {
		for (IDisplaySelectionListener listener : listeners)
			try {
				listener.smallButtonPressed(this, largeButtonDefn, smallButtonDefn);
			} catch (Exception e) {
				try {
					exceptionHandler.process(e);
				} catch (Exception e1) {
					WrappedException.wrap(e1);
				}
			}
	}

	public List<String> getVisibleSmallButtonsId() {
		return Maps.getOrException(smallButtonSelectionMap, largeButtonSelectedId);
	}

}
