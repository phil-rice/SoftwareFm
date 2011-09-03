package org.softwareFm.displayCore.api.impl;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Shell;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryBuilder;
import org.softwareFm.displayCore.api.IEditor;
import org.softwareFm.displayCore.api.ILineEditor;
import org.softwareFm.displayCore.api.IValidator;
import org.softwareFm.displayText.TextDisplayer;
import org.softwareFm.swtBasics.SwtTestFixture;
import org.softwareFm.utilities.tests.Tests;

public class DisplayContainerFactoryBuilderInternalValidationTest extends TestCase {

	private IDisplayContainerFactoryBuilder factory;

	public void testCannotDuplicateDisplayName() {
		factory.registerDisplayer("key1", new TextDisplayer());
		factory.registerDisplayer("key2", new TextDisplayer());
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				factory.registerDisplayer("key1", new TextDisplayer());
			}
		});
		assertEquals("Cannot have duplicate value for key key1. Existing value TextDisplayer. New value TextDisplayer", e.getMessage());
	}

	public void testCannotDuplicateEditorName() {
		factory.registerEditor("key1", IEditor.Utils.noEditor());
		factory.registerEditor("key2", IEditor.Utils.noEditor());
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				factory.registerEditor("key1", IEditor.Utils.noEditor());
			}
		});
		assertEquals("Cannot have duplicate value for key key1. Existing value noEditor. New value noEditor", e.getMessage());
	}

	public void testCannotHaveDuplicateLineEditors() {
		factory.registerLineEditor("key1", ILineEditor.Utils.noLineEditor());
		factory.registerLineEditor("key2", ILineEditor.Utils.noLineEditor());
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				factory.registerLineEditor("key1", ILineEditor.Utils.noLineEditor());
			}
		});
		assertEquals("Cannot have duplicate value for key key1. Existing value noLineEditor. New value noLineEditor", e.getMessage());

	}

	public void testCannotHaveDuplicateValidators() {
		factory.registerValidator("key1", IValidator.Utils.noValidator());
		factory.registerValidator("key2", IValidator.Utils.noValidator());
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				factory.registerValidator("key1", IValidator.Utils.noValidator());
			}
		});
		assertEquals("Cannot have duplicate value for key key1. Existing value noValidator. New value noValidator", e.getMessage());
	}

	private Shell shell;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = SwtTestFixture.shell();
		factory = IDisplayContainerFactoryBuilder.Utils.factoryBuilder();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		shell.dispose();
	}

}
