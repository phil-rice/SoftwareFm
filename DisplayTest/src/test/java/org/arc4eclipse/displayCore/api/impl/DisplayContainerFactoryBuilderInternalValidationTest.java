package org.arc4eclipse.displayCore.api.impl;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.api.IEditor;
import org.arc4eclipse.displayCore.api.ILineEditor;
import org.arc4eclipse.displayCore.api.IValidator;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.tests.Tests;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class DisplayContainerFactoryBuilderInternalValidationTest extends TestCase {

	private IDisplayContainerFactoryBuilder factory;
	private final IFunction1<Device, Image> nullImageMaker = null;

	public void testCanOnlyUseRegisteredEntites() {
		factory.registerEntity("entity");
		Tests.assertThrows(true, IllegalArgumentException.class, new RegisterEntity("entity"));
		Tests.assertThrows(false, IllegalArgumentException.class, new RegisterEntity("unknown"));
		Tests.assertThrows(true, IllegalArgumentException.class, new RegisterEntityWithImageMaker("entity", nullImageMaker));
		Throwable e = Tests.assertThrows(false, IllegalArgumentException.class, new RegisterEntityWithImageMaker("unknown", nullImageMaker));
		assertEquals("Illegal Entity name unknown. Known values are [entity]", e.getMessage());
	}

	public void testCannotDuplicateDisplayName() {
		factory.registerDisplayer("key1", new DisplayText());
		factory.registerDisplayer("key2", new DisplayText());
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				factory.registerDisplayer("key1", new DisplayText());
			}
		});
		assertEquals("Cannot have duplicate value for key key1. Existing value DisplayText. New value DisplayText", e.getMessage());
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

	private final AtomicInteger count = new AtomicInteger();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = IDisplayContainerFactoryBuilder.Utils.factoryBuilder();
	}

	private final class RegisterEntity implements Runnable {
		private final String entity;

		public RegisterEntity(String entity) {
			this.entity = entity;
		}

		@Override
		public void run() {
			int i = count.incrementAndGet();
			factory.registerForEntity(entity, "key" + i, "title" + i, "help" + i);
		}
	}

	private final class RegisterEntityWithImageMaker implements Runnable {
		private final String entity;
		private final IFunction1<Device, Image> imageMaker;

		public RegisterEntityWithImageMaker(String entity, IFunction1<Device, Image> imageMaker) {
			this.entity = entity;
			this.imageMaker = imageMaker;
		}

		@Override
		public void run() {
			int i = count.incrementAndGet();
			factory.registerForEntity(entity, "key" + i, "title" + i, "help" + i, imageMaker);
		}
	}

}
