package org.softwarefm.display;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.impl.SmallButtonDefn;

public class DisplaySelectionListenerMock implements IDisplaySelectionListener {

	public final List<LargeButtonDefn> largeButtonDefns = Lists.newList();
	public final List<SmallButtonDefn> smallButtonDefns = Lists.newList();
	public final List<DisplaySelectionModel> models = Lists.newList();

	@Override
	public void smallButtonPressed(DisplaySelectionModel model, LargeButtonDefn largeButtonDefn, SmallButtonDefn smallButtonDefn) {
		this.models.add(model);
		this.largeButtonDefns.add(largeButtonDefn);
		this.smallButtonDefns.add(smallButtonDefn);
	}

}
