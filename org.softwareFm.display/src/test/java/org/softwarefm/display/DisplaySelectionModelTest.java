package org.softwarefm.display;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwarefm.display.actions.ActionMock;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.impl.LargeButtonDefn;
import org.softwarefm.display.impl.SmallButtonDefn;
import org.softwarefm.display.smallButtons.SmallButtonFactoryMock;

public class DisplaySelectionModelTest extends TestCase {

	private ActionMock action1;
	private SmallButtonFactoryMock smallButtonFactory;
	private SmallButtonDefn defn1a;
	private SmallButtonDefn defn1b;
	private SmallButtonDefn defn2a;
	private SmallButtonDefn defn2b;
	private SmallButtonDefn defn2c;
	private LargeButtonDefn largeDefn1;
	private LargeButtonDefn largeDefn2;

	public void testConstructor() {
		DisplaySelectionModel model = new DisplaySelectionModel(ICallback.Utils.rethrow(), largeDefn1, largeDefn2);
		checkButtons(model, "1", "1.a", "1.b");
	}

	public void testSelectInformsListeners() {
		DisplaySelectionModel model = new DisplaySelectionModel(ICallback.Utils.rethrow(), largeDefn1, largeDefn2);
		DisplaySelectionListenerMock listener1 = new DisplaySelectionListenerMock();
		DisplaySelectionListenerMock listener2 = new DisplaySelectionListenerMock();
		model.addDisplaySelectionListener(listener1);
		model.addDisplaySelectionListener(listener2);
		model.select("2.a");
		assertEquals(Arrays.asList(largeDefn2), listener1.largeButtonDefns);
		assertEquals(Arrays.asList(largeDefn2), listener2.largeButtonDefns);
		assertEquals(Arrays.asList(defn2a), listener1.smallButtonDefns);
		assertEquals(Arrays.asList(defn2a), listener2.smallButtonDefns);
		assertEquals(Arrays.asList(model), listener1.models);
		assertEquals(Arrays.asList(model), listener2.models);
	}

	public void testSelectChangesLargeButtonWhenSmallButtonFromDifferentLargeButtonIsPressed() {
		DisplaySelectionModel model = new DisplaySelectionModel(ICallback.Utils.rethrow(), largeDefn1, largeDefn2);
		model.select("2.a");
		checkButtons(model, "2", "2.a", "2.b", "2.c");
		model.select("1.b");
		checkButtons(model, "1", "1.a", "1.b");
		model.select("2.c");
		checkButtons(model, "2", "2.a", "2.b", "2.c");
	}

	public void testSelectChangesButtonVisibilityWhenSmallButtonFromSelectedLargeButtonIsPressed() {
		DisplaySelectionModel model = new DisplaySelectionModel(ICallback.Utils.rethrow(), largeDefn1, largeDefn2);
		model.select("2.a");
		checkButtons(model, "2", "2.a", "2.b", "2.c");
		model.select("2.a");
		checkButtons(model, "2", "2.b", "2.c");
		model.select("2.b");
		checkButtons(model, "2", "2.c");
		model.select("2.a");
		checkButtons(model, "2", "2.a", "2.c");
		model.select("2.b");
		checkButtons(model, "2", "2.a", "2.b", "2.c");
	}
	
	public void testRemembersSelectionStateWhenSwappingLargeButtons(){
		DisplaySelectionModel model = new DisplaySelectionModel(ICallback.Utils.rethrow(), largeDefn1, largeDefn2);
		model.select("2.a");
		model.select("2.a");
		checkButtons(model, "2", "2.b", "2.c");
		model.select("1.a");
		model.select("2.a");
		checkButtons(model, "2", "2.b", "2.c");
	}

	private void checkButtons(DisplaySelectionModel model, String largeButtonId, String... smallButtonIds) {
		assertEquals(largeButtonId, model.getLargeButtonSelectedId());
		assertEquals(Arrays.asList(smallButtonIds), model.getVisibleSmallButtonsId());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		action1 = new ActionMock("1");
		ActionStore store = new ActionStore().//
				action("action1", action1);
		smallButtonFactory = new SmallButtonFactoryMock("1");
		defn1a = new SmallButtonDefn("1.a", "imageId", store, smallButtonFactory);
		defn1b = new SmallButtonDefn("1.b", "imageId", store, smallButtonFactory);
		defn2a = new SmallButtonDefn("2.a", "imageId", store, smallButtonFactory);
		defn2b = new SmallButtonDefn("2.b", "imageId", store, smallButtonFactory);
		defn2c = new SmallButtonDefn("2.c", "imageId", store, smallButtonFactory);

		largeDefn1 = new LargeButtonDefn("1", defn1a, defn1b);
		largeDefn2 = new LargeButtonDefn("2", defn2a, defn2b, defn2c);
	}
}
