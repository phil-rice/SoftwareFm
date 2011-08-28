package org.arc4eclipse.displayCore.api.impl;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.DisplayerMock;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.api.IDisplayContainerForTests;
import org.arc4eclipse.displayCore.api.IEditor;
import org.arc4eclipse.displayCore.api.IValidator;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.swtBasics.SwtTestFixture;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.maps.Maps;
import org.arc4eclipse.utilities.tests.Tests;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DisplayContainerFactoryTest extends TestCase {

	private final DisplayerMock displayerA = new DisplayerMock("A");
	private final DisplayerMock displayerB = new DisplayerMock("B");
	private final IEditor editor1 = IEditor.Utils.noEditor();
	private final IEditor editor2 = IEditor.Utils.noEditor();
	private final IValidator validator1 = IValidator.Utils.noValidator();
	private final IValidator validator2 = IValidator.Utils.noValidator();

	private IDisplayContainerFactoryBuilder builder;
	private Shell shell;
	private IDisplayContainerFactory factory;
	private DisplayerContext displayerContext;

	public void testCreateProducesNewObjects() {
		IDisplayContainerFactory factory1a = builder.build("entity1");
		IDisplayContainerFactory factory1b = builder.build("entity1");
		IDisplayContainerFactory factory2 = builder.build("entity2");
		assertNotSame(factory1a, factory1b);
		assertNotSame(factory1a, factory2);
	}

	public void testMakeTwoContainersAndCheckThatTheChildControlsAreUnique() {
		IDisplayContainerForTests container0 = (IDisplayContainerForTests) factory.create(displayerContext, shell);
		IDisplayContainerForTests container1 = (IDisplayContainerForTests) factory.create(displayerContext, shell);

		List<Control> children0 = Swts.allChildren(container0.getComposite());
		List<Control> children1 = Swts.allChildren(container1.getComposite());
		for (Control l : children0)
			assertFalse(children1.contains(l));
		for (Control l : children1)
			assertFalse(children0.contains(l));
	}

	public void testThrowsExceptionsWhenInsufficentDetails() {
		checkNoKey("Must have a value for key in map {}");
		checkNoKey("Must have a value for title in map {key=key_new}", DisplayCoreConstants.key, "key_new");
		checkNoKey("Must have a value for help in map {key=key_new, title=title_new}", DisplayCoreConstants.key, "key_new", DisplayCoreConstants.title, "title_new");
		checkNoKey("Must have a value for help in map {key=key_new, title=title_new}", DisplayCoreConstants.key, "key_new", DisplayCoreConstants.title, "title_new");
		checkNoKey("Must have a value for displayer in map {key=key_new, title=title_new, help=helpA}", DisplayCoreConstants.key, "key_new", DisplayCoreConstants.title, "title_new", DisplayCoreConstants.help, "helpA");
	}

	private void checkNoKey(String message, String... data) {
		final Map<String, String> dataMap = Maps.makeLinkedMap((Object[]) data);
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				factory.register(dataMap);
			}
		});
		assertEquals(message, e.getMessage());
	}

	public void testDisplayerDetailsAreSetCorrectly() {
		factory.create(displayerContext, shell);

		assertEquals(1, displayerA.smallDisplayerDetails.size());
		assertEquals(1, displayerA.largeDisplayerDetails.size());
		assertEquals(1, displayerB.smallDisplayerDetails.size());
		assertEquals(1, displayerB.largeDisplayerDetails.size());

		DisplayerDetails displayerDetailsA = displayerA.smallDisplayerDetails.get(0);
		DisplayerDetails displayerDetailsB = displayerB.smallDisplayerDetails.get(0);

		assertSame(displayerDetailsA, displayerA.largeDisplayerDetails.get(0));
		assertSame(displayerDetailsB, displayerB.largeDisplayerDetails.get(0));

		checkDisplayerDetails(displayerDetailsA, "entity1", "keyA");
		checkDisplayerDetails(displayerDetailsB, "entity1", "keyB");
	}

	public void testDisplayerContextSent() {
		factory.create(displayerContext, shell);
		assertSame(displayerContext, displayerA.displayerContext);
		assertSame(displayerContext, displayerB.displayerContext);
	}

	private void checkDisplayerDetails(DisplayerDetails details, String entity, String key) {
		assertEquals(entity, details.entity);
		assertEquals(key, details.key);
	}

	public void testButtonsAreAtTopFollowedByLargeControls() {
		IDisplayContainerForTests container0 = (IDisplayContainerForTests) factory.create(displayerContext, shell);
		Swts.assertHasChildrenInOrder(container0.getComposite(), container0.compButtons(), displayerA.largeControls.get(0), displayerB.largeControls.get(0));
	}

	public void testSmallButtonsAreInCorrectOrder() {
		IDisplayContainerForTests container0 = (IDisplayContainerForTests) factory.create(displayerContext, shell);
		Swts.assertHasChildrenInOrder(container0.compButtons(), displayerA.smallControls.get(0), displayerB.smallControls.get(0));
	}

	public void testSetValues() {
		DisplayerContext displayerContext = null;
		IDisplayContainer container = factory.create(displayerContext, shell);
		Map<String, Object> data = Maps.makeMap("keyA", "value1", "keyB", "value2");
		Map<String, Object> context = Maps.makeMap(RepositoryConstants.entity, "entity");
		BindingContext bindingContext = new BindingContext(RepositoryDataItemStatus.FOUND, null, data, context);

		container.setValues(bindingContext);

		assertEquals("value1", displayerA.largeControls.get(0).getText());
		assertEquals("value2", displayerB.largeControls.get(0).getText());
	}

	public void testMustHaveDisplayer() {
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				factory.register(Maps.<String, String> makeMap("key", "keyA", "displayer", "dispUnregistered", "title", "titleA", "help", "helpA", "editor", "editor1"));
			}
		});
		assertEquals("Illegal value for displayer dispUnregistered. Legal values are [disp1, disp2] in map {editor=editor1, help=helpA, title=titleA, displayer=dispUnregistered, key=keyA}", e.getMessage());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = SwtTestFixture.shell();

		builder = IDisplayContainerFactoryBuilder.Utils.factoryBuilder();
		builder.registerDisplayer("disp1", displayerA);
		builder.registerDisplayer("disp2", displayerB);
		builder.registerEditor("editor1", editor1);
		builder.registerEditor("editor2", editor2);
		builder.registerValidator("validator1", validator1);
		builder.registerValidator("validator2", validator2);

		factory = builder.build("entity");
		factory.register(Maps.<String, String> makeMap("key", "keyA", "displayer", "disp1", "title", "titleA", "help", "helpA", "editor", "editor1"));
		factory.register(Maps.<String, String> makeMap("key", "keyB", "displayer", "disp2", "title", "titleB", "help", "helpB", "editor", "editor2"));
		displayerContext = null;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		shell.dispose();
	}

}
