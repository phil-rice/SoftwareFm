/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.navigation.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.card.navigation.internal.NavHistoryCombo;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.history.History;

public class NavHistoryComboTest extends SwtTest {

	private ImageRegistry imageRegistry;
	private MemoryCallback<Integer> memory;
	private NavNextHistoryPrevConfig<Integer> config;
	private NavHistoryCombo<Integer> nav;
	private Combo combo;
	private History<Integer> history;

	public void testHistoryChangesUpdateCombo() {
		dispatchUntilQueueEmpty();
		checkComboItems();
		
		history.push(1);
		dispatchUntilQueueEmpty();
		checkComboItems("1_");
		
		history.push(2);
		dispatchUntilQueueEmpty();
		checkComboItems("1_","2_");
	}

	private void checkComboItems(String... expected) {
		List<String> actuals = Lists.newList();
		for (String item : combo.getItems())
			actuals.add(item);
		assertEquals(Arrays.asList(expected), actuals);
	}

	public void testHistoryUpdatingDoesntCallCallback() {
		dispatchUntilQueueEmpty();
		assertEquals(Arrays.<Integer>asList(), memory.getResult());
		
		history.push(1);
		dispatchUntilQueueEmpty();
		assertEquals(Arrays.<Integer>asList(), memory.getResult());
	}
	
	public void testComboBeingSelectedCallsCallback(){
		history.push(1);
		history.push(2);
		dispatchUntilQueueEmpty();
		assertEquals(Arrays.<Integer>asList(), memory.getResult());
		
		combo.select(1);//this has a value of 2
		combo.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();
		assertEquals(Arrays.<Integer>asList(2), memory.getResult());

		combo.select(0);
		combo.notifyListeners(SWT.Selection, new Event());
		dispatchUntilQueueEmpty();
		assertEquals(Arrays.<Integer>asList(2,1), memory.getResult());
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		memory = ICallback.Utils.memory();
		new BasicImageRegisterConfigurator().registerWith(shell.getDisplay(), imageRegistry = new ImageRegistry());
		config = new NavNextHistoryPrevConfig<Integer>(1000, Swts.imageFn(imageRegistry), Functions.<Integer> addToEnd("_"), memory);// height irrelevant
		history = new History<Integer>();
		nav = new NavHistoryCombo<Integer>(shell, history, memory, config);
		combo = (Combo) nav.getControl();
	}

}