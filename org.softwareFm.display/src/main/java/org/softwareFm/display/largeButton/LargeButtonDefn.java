package org.softwareFm.display.largeButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.softwareFm.display.smallButtons.SmallButtonDefn;

public class LargeButtonDefn {

	public final List<SmallButtonDefn> defns;
	public final String id;

	public LargeButtonDefn(String id, SmallButtonDefn... defns) {
		this.id = id;
		this.defns = Collections.unmodifiableList(new ArrayList<SmallButtonDefn>(Arrays.asList(defns)));
	}
}
