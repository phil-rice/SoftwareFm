package org.softwarefm.display.impl;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.NameAndValue;
import org.softwarefm.display.ActionDefn;
import org.softwarefm.display.SoftwareFmLayout;
import org.softwarefm.display.actions.ActionContext;
import org.softwarefm.display.actions.ActionMock;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.composites.TitleAndText;
import org.softwarefm.display.data.DataGetterMock;
import org.softwarefm.display.data.ResourceGetterMock;
import org.softwarefm.display.displayer.TextDisplayer;
import org.softwarefm.display.smallButtons.SimpleImageControl;

public class DisplayerDefnIntegrationTest extends TestCase {

	private Shell shell;
	private DisplayerDefn displayerDefn;
	private ActionContext actionContext;
	private ActionStore actionStore;
	private ActionMock actionMock;

	public void testCreateWithNoButtons() {
		TitleAndText displayer = (TitleAndText) displayerDefn.createDisplayer(shell, actionStore, actionContext);
		checkButtons(displayer);
		assertEquals("registeredTitle", displayer.getTitle());
	}

	public void testWithOneButton() {
		DisplayerDefn dispDefnWithButton = displayerDefn.actions(new ActionDefn("someId", ArtifactsAnchor.projectKey, OverlaysAnchor.deleteKey).tooltip("tooltip0"));
		TitleAndText displayer = (TitleAndText) dispDefnWithButton.createDisplayer(shell, actionStore, actionContext);
		assertEquals("registeredTitle", displayer.getTitle());
		checkButtons(displayer, ArtifactsAnchor.projectKey + ":" + OverlaysAnchor.deleteKey);
	}

	public void testWithTwoButton() {
		DisplayerDefn dispDefnWithButton = displayerDefn.actions(//
				new ActionDefn("someId", ArtifactsAnchor.projectKey, OverlaysAnchor.deleteKey).tooltip("tooltip0"),//
				new ActionDefn("someId", ArtifactsAnchor.projectKey, null).tooltip("tooltip1"));
		TitleAndText displayer = (TitleAndText) dispDefnWithButton.createDisplayer(shell, actionStore, actionContext);
		assertEquals("registeredTitle", displayer.getTitle());
		checkButtons(displayer, ArtifactsAnchor.projectKey + ":" + OverlaysAnchor.deleteKey, ArtifactsAnchor.projectKey);
	}
	
	@SuppressWarnings("unchecked")
	public void testPressingButtonCausesActionToFireWithParametersFromDataGetter(){
		DisplayerDefn dispDefnWithButton = displayerDefn.//
				actions(new ActionDefn("someId", ArtifactsAnchor.projectKey, OverlaysAnchor.deleteKey).params("a"));
		TitleAndText displayer = (TitleAndText) dispDefnWithButton.createDisplayer(shell, actionStore, actionContext);
		Control[] children = displayer.getButtonComposite().getChildren();
		SimpleImageControl control = (SimpleImageControl) children[0];
		control.notifyListeners(SWT.MouseDown, new Event());
		assertEquals(Arrays.asList(displayer), actionMock.displayers);
		assertEquals(Arrays.asList(Arrays.asList("a")), actionMock.formalParams);
		assertEquals(Arrays.asList(Arrays.asList(1)), actionMock.actualParams);
		
	}

	private void checkButtons(TitleAndText displayer, String... mainAndBackground) {
		Control[] children = displayer.getButtonComposite().getChildren();
		assertEquals(mainAndBackground.length, children.length);
		for (int i = 0; i < children.length; i++) {
			SimpleImageControl control = (SimpleImageControl) children[i];
			assertEquals(false, control.value());
			NameAndValue nameAndValue = NameAndValue.fromString(mainAndBackground[i]);
			assertEquals(nameAndValue.name, control.config.mainImage);
			assertEquals(nameAndValue.value, control.config.overlayImage);
			assertEquals("tooltip"+ i, control.getToolTipText());
		}
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = new Shell();
		actionMock = new ActionMock("someAction");
		actionStore = new ActionStore().action("someId", actionMock);
		ImageRegistry imageRegistry = new ImageRegistry();
		new BasicImageRegisterConfigurator().registerWith(shell.getDisplay(), imageRegistry);
		IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(new ResourceGetterMock("someTitle", "registeredTitle"));
		actionContext = new ActionContext(new DataGetterMock("a", 1), new CompositeConfig(shell.getDisplay(), new SoftwareFmLayout(), imageRegistry, resourceGetter), null);
		displayerDefn = new DisplayerDefn(new TextDisplayer(true)).title("someTitle");

	}

	@Override
	protected void tearDown() throws Exception {
		shell.dispose();
		super.tearDown();
	}
}